(ns rqnpc.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType])
    (:import goog.History))

(def state (reagent/atom {:characters []}))

(defn update-state! [f & args]
  (apply swap! state update-in [:characters] f args))

(defn insert-character! [c]
  (update-state! conj c))

(defn roll [sides]
  (+ 1 (rand-int sides)))

(def abilities [:strength :stamina :power :dexterity :charisma :intelligence])

(defn roll-ability []
  (reduce + (repeat 3 (roll 6))))

(defn generate-abilities []
  (into
   (hash-map)
   (mapcat (fn [ability] {ability (roll-ability)}) abilities)))
(defn generate-size [] {:size (+ (roll 6) (roll 6) 6)})
(defn generate-health [size stamina] {:health (int (/ (+ size stamina) 2))})

(defn generate-npc []
  (let [abilities (generate-abilities)
        size (generate-size)
        health (generate-health (:size size) (:stamina abilities))]
    (insert-character! (reduce conj [abilities size health]))))
;; -------------------------
;; Views

(defn render-character [character]
   (map (fn [[key val]] [:p (str key " " val)]) character))

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
  (mount-root))
