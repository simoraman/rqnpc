(ns rqnpc.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [reagent-forms.core :refer [bind-fields]]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [rqnpc.character :as character]
              [ajax.core :refer [GET]])
    (:import goog.History))

(def weapons (reagent/atom {}))

(defn deserialize-weapons [response]
  (->> response
       (js->clj)
       (reset! weapons)
       ))

(defn load-weapons []
  (GET "/data/weapons.json" {:response-format :json
                             :keywords? true
                             :handler deserialize-weapons}))

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

(defn set-weapon [character weapon-name weapons]
  (let [weapon (first (filter #(= weapon-name (:Weapon %)) weapons))
        char (assoc character :weapon weapon)]
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

(defn render-character [character weapons]
  [:div {:class "character-sheet"}
   [:div [:button {:on-click #(remove-character! character)} "Delete"]]
   [:div {:class "abilities"}
   (map
    (fn [[key val]]
      (if (not= :health key) [:p (str (name key) " " val)] "")) character)]
   [:div {:class "health"}
    "Health: " (:health character)
    [atom-input hit-amount]
    [:button {:on-click #(hit-for @hit-amount character)} "Hit!"]]
   [:div
    [:select.form-control {:value (:weapon character)
                           :on-change #(set-weapon character (-> % .-target .-value) weapons)}
          (for [weapon weapons]
            [:option {:value (:Weapon weapon)} (:Weapon weapon)])]]
   [:div [:h3 "Strike Rank"]
    [:span (str "Size: " (:size-strike-rank character))]
    [:span (str "Dexterity: " (:dexterity-size-rank character))]
    [:span (str "Weapon: " (-> character :weapon :StrikeRank))]]])

(defn home-page []
  [:div [:h2 "Runequest NPC generator"]
   [:div
    [:button {:on-click #(generate-npc)} "Generate"]]
   (doall (for [c (:characters @state)]
            (render-character c @weapons)))])

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (load-weapons)
  (reset! state (loadState))
  (mount-root))
