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

(defn new-npc []
  (let [abilities (generate-abilities)
        size (generate-size)
        health (generate-health (:size size) (:stamina abilities))]
    (reduce conj [abilities size health])))
