(ns rqnpc.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [rqnpc.character :as character])
    (:import goog.History))

(def state (reagent/atom {:characters []}))

(defn update-state! [f & args]
  (apply swap! state update-in [:characters] f args))

(defn insert-character! [c]
  (update-state! conj c))

(defn generate-npc [] (insert-character! (character/new-npc)))
;; -------------------------
;; Views

(defn render-character [character]
  (map (fn [[key val]] [:p (str (name key) " " val)]) character))

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
