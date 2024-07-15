(ns rwaweber.repl
  (:import [java.net Socket])
  (:require [rebel-readline.clojure.main :as rebel]
            [clojure.tools.logging :as log]
            [nrepl.server :refer [start-server stop-server]]))

(defn high-port []
  (+ 62000 (rand-int 3535)))

(defn is-file?
  [filename]
  (try (slurp filename)
       true
       (catch java.io.FileNotFoundException _
         false)))

(defn find-unused-addr
  "will attempt to find an unused port

  will check if .nrepl-port is present and return that"
  ([repl-port]
   (let [host "127.0.0.1"]
     (try (Socket. host repl-port)
          (log/infof
           "another service already listening on %s:%d, trying another" host repl-port)
          (find-unused-addr (high-port))
          (catch java.net.ConnectException _
            {:bind host
             :port repl-port}))))
  ([]
   (if (is-file? ".nrepl-port")
     (let [port (read-string (slurp ".nrepl-port"))]
       (log/infof "found .nrepl-port file, suggesting port %d", port)
       (find-unused-addr port))
     (let [port (high-port)]
       ;; write out port to file
       (log/infof "no .nrepl-port file found, using %d", port)
       (spit ".nrepl-port" port)
       (find-unused-addr port)))))

(defn -main
  [& _]
  (let [{:keys [bind port]} (find-unused-addr)
        repl-server (start-server :bind bind
                                  :port port)]
    (log/infof "starting rwaweber.repl on %s:%d" bind port)
    (rebel/-main)
    (stop-server repl-server)))
