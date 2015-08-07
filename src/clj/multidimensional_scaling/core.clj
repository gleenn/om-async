(ns multidimensional-scaling.core)

(defn zip [& xs]
  (apply map (fn [& points] (apply vector points)) xs))

(defn distance [p1 p2]
  (Math/sqrt (reduce +
                     (for [i (range (count p1))]
                       (let [p1i (get p1 i)
                             p2i (get p2 i)
                             delta (- p1i p2i)]
                         (* delta delta))))))

(defn distance-matrix
  "Given a set of points, return a 2D matrix of distances between given points"
  [points]
  (map (fn [y] (apply vector (map (fn [x] (distance x y)) points))) points))

;(= 1.4142135623730951 (distance [1 1] [2 2]))

(distance-matrix [[0 0] [1 1]])