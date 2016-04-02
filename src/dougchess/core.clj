(ns dougchess.core
  [:require [dougchess.communicate :as comm]]
  (:gen-class))

(defn -main
  "Connects client to framework on port 54361, with name Doug."
  [& args]
  (println "Connecting to framework...")
  (future 
    (comm/start-communication 54361 "Doug" :debug-print true)))
