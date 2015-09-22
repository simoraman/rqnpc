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
(defn- generate-size [] {:size (+ (roll 6) (roll 6) 6)})
(defn- generate-health [size stamina] {:health (int (/ (+ size stamina) 2))})
(defn- generate-size-strike-rank [size]
  {:size-strike-rank (cond
                       (> size 19) 0
                       (> size 15) 1
                       (> size 9) 2
                       :else 3)})
(defn- generate-dexterity-strike-rank [dex]
  {:dexterity-size-rank (cond
                          (> dex 19) 1
                          (> dex 15) 2
                          (> dex 9) 3
                          :else 4)})

(defn new-npc []
  (let [abilities (generate-abilities)
        size (generate-size)
        health (generate-health (:size size) (:stamina abilities))
        size-strike-rank (generate-size-strike-rank (:size size))
        dex-strike-rank (generate-dexterity-strike-rank (:dexterity abilities))]
    (reduce conj [abilities size health size-strike-rank dex-strike-rank])))
