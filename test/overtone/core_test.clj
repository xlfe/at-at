(ns overtone.core-test
  (:require [overtone.at-at :as aa]
            [clojure.test :as t])
  (:import (java.time Instant Duration)
           (java.time.temporal ChronoUnit)))

(defn check-timeliness!
  "Checks whether the chimes actually happended at the time for they were scheduled."
  [proof]
  (doseq [[value taken-at] proof
          :let [diff (->> [value taken-at]
                          (map #(.toEpochMilli ^Instant %))
                          (apply -)
                          (Math/abs))]]
    (t/is (< diff 5)
          (format "Expected to run at ±%s but run at %s, i.e. diff of %dms" value taken-at diff))))

(t/deftest test-at
  (let [times [
               [0                  (Instant/now)]
               [1000 (.plusSeconds (Instant/now) 1)]
               [2000 (.plusSeconds (Instant/now) 2)]]
        proof (atom [])
        pool  (aa/mk-pool)]
    (doall 
      (map
        (fn [[a e]]
          (aa/after 
            a 
            (fn []
              (swap! proof conj [e (Instant/now)]))
            pool))
        times))
      
                           
    (Thread/sleep 2500)
    (check-timeliness! @proof)))


(t/deftest test-error
  (let [proof (atom [])
        pool (aa/mk-pool)
        sched (aa/after 
                500
                (fn []
                  (t/is
                    (thrown? Exception
                      (do
                        (swap! proof conj (Instant/now))
                        (throw (ex-info "boom!" {:time 500}))))))
                pool)]
    (Thread/sleep 1000)
    (t/is (= 1 (count @proof)))))

