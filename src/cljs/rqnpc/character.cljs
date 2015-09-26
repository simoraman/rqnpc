(ns rqnpc.character)

(defn- roll [sides]
  (+ 1 (rand-int sides)))

(def abilities [:strength :stamina :power :dexterity :charisma :intelligence])

(defn- roll-ability []
  (reduce + (repeat 3 (roll 6))))

(defn- generate-abilities []
  (into
   (hash-map)
   (mapcat (fn [ability] {ability (roll-ability)}) abilities)))
(defn- generate-size [] (+ (roll 6) (roll 6) 6))
(defn- generate-health [size stamina] (int (/ (+ size stamina) 2)))
(defn- generate-size-strike-rank [size]
  (cond
    (> size 19) 0
    (> size 15) 1
    (> size 9) 2
    :else 3))
(defn- generate-dexterity-strike-rank [dex]
  (cond
    (> dex 19) 1
    (> dex 15) 2
    (> dex 9) 3
    :else 4))

(defrecord Character [abilities health size-strike-rank dexterity-size-rank])

(defn new-npc []
  (let [size (generate-size)
        abilities (conj (generate-abilities) {:size size})
        health (generate-health size (:stamina abilities))
        size-strike-rank (generate-size-strike-rank (:size size))
        dex-strike-rank (generate-dexterity-strike-rank (:dexterity abilities))]
    (Character. abilities health size-strike-rank dex-strike-rank)))
