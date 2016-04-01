(ns dougchess.communicate
  (:require [clojure.data.json :as json]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]])
  (:import (org.zeromq ZMQ))
  (:gen-class))

(def context-handle (atom nil))
(def socket-handle (atom nil))

(defn connect
  "Open connection to minichess framework on port port.
   Set context-handle and socket-handle to hold this port."
  [port]
  (let [context (ZMQ/context 1)
        socket (.socket context(ZMQ/PAIR))]
        (.bind socket (str "tcp://*:" port))
        (reset! context-handle context)
        (reset! socket-handle socket)
        true))

(defn close
  "Close the connection if it is open, reset context-handle
  and socket-handle"
  []
  (if @socket-handle
    (.close @socket-handle))
  (if @context-handle
    (.close @context-handle))
  (reset! socket-handle nil)
  (reset! context-handle nil))

