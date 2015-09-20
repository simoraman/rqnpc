(ns rqnpc.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [rqnpc.character :as character])
    (:import goog.History))

(defn saveState [obj]
  (->> obj
       (clj->js)
       (js/JSON.stringify)
       (.setItem js/localStorage "state")))

(defn loadState []
  (let [stateJson (.getItem js/localStorage "state")]
    (->> stateJson
         (js/JSON.parse)
         (js->clj)
         (clojure.walk/keywordize-keys))))

(def state (reagent/atom {:characters []}))

(defn update-state! [f & args]
  (do
    (apply swap! state update-in [:characters] f args)
    (saveState @state)))

(defn insert-character! [c]
  (update-state! conj c))

(defn remove-character! [c]
  (update-state! (fn [cs]
                      (vec (remove #(= % c) cs)))
                    c))

(defn generate-npc [] (insert-character! (character/new-npc)))
(defn hit-for [amount character]
  (let [char (assoc character :health (- (:health character) amount))]
    (do
      (remove-character! character)
      (insert-character! char))))

;; -------------------------
;; Views

(defn atom-input [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])
(def hit-amount (reagent/atom 0))
(defn render-character [character]
  [:div {:class "character-sheet"}
   [:div [:button {:on-click #(remove-character! character)} "Delete"]]
   [:div {:class "abilities"}
   (map
    (fn [[key val]]
      (if (not= :health key) [:p (str (name key) " " val)] "")) character)
    ]
   [:div {:class "health"}
    "Health: " (:health character) [atom-input hit-amount] [:button {:on-click #(hit-for @hit-amount character)} "Hit!"]]])

(defn home-page []
  [:div [:h2 "Runequest NPC generator"]
   [:div
    [:button {:on-click #(generate-npc)} "Generate"]]
   (for [c (:characters @state)]
     (render-character c))])

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (reset! state (loadState))
  (mount-root))
