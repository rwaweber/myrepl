(ns rwaweber.repl
  (:require [clojure.string :as string]
            [rebel-readline.clojure.main :as rebel]
            [nrepl.server :refer [start-server stop-server]]))

(def repl-port 12345)

(defonce myserver
  (start-server
   :port repl-port
   :bind "127.0.0.1"))

(defn -main
  [& args]
  (println "starting rwaweber.repl on port " repl-port)
  (rebel/-main)
  (stop-server myserver))
