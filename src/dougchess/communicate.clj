;; Henry Cooney <hacoo36@gmail.com> <Github: hacoo>
;; 1 Apr. 2016 :)
;; dougchess/communicate

;; Communication module for Dougchess. Connects / communicates with 
;; Minichess framework using  synchronous zeromq socket.

;; Communication is meant to be very simple, and is basically 
;; the main loop of the program. When a packet is received, it should
;; be acted on immediately. Other threads will attempt to precompute
;; moves in the background; this information can be taken advantage
;; of when a message arrives.

(ns dougchess.communicate
  (:require [clojure.data.json :as json]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]
            [com.keminglabs.zmq-async.core :as zmq]
            [clojure.string :refer [join]]
            [dougchess.board :as board]
            )
  (:import (org.zeromq ZMQ))
  (:gen-class))

(def context-handle (atom nil))
(def socket-handle (atom nil))
(def me (atom nil)) ;; name to register with minichess
(def continue? (atom true))

(defn connect
  "Open connection to minichess framework on port port.
   Set context-handle and socket-handle to hold this port."
  [port]
  (let [context (ZMQ/context 1)
        socket (.socket context ZMQ/PAIR)]
    (.bind socket (str "tcp://*:" port))
    (reset! context-handle context)
    (reset! socket-handle socket)
    true))

(defn close-socket
  "Close the connection if it is open, reset context-handle
  and socket-handle"
  []
  (reset! continue? false)
  (if @socket-handle
    (.close @socket-handle))
  (if @context-handle
    (.close @context-handle))
  (reset! socket-handle nil)
  (reset! context-handle nil))

(defn print-message
  "Print and incoming or outgoing message in a nice & pretty way."
  [msg dir]
  (let [header (case dir
                 :in  "  INCOMING MESSAGE:  "
                 :out "  SENT MESSAGE:      ")]
    (print (str "----------------------------\n"
                header "\n"
                "  " msg "\n"
                "----------------------------\n"))))

(defn get-next-string
  "Recieve the next string on the ZMQ socket handle socket. 
  Print it if the option debug-print is present"
  [socket & {:keys [debug-print]} ]
  (let [recvd (.recvStr socket)]
    (if (true? debug-print) (print-message recvd :in))
    recvd))

(defn get-next-json
  "Recieve the next string on the ZMQ socket handle socket. 
  Convert it from json to a Clojure map and return.
  Print it if the option debug-print is present"
  [socket & {:keys [debug-print]} ]
  (let [recvd (.recvStr socket)]
    (if (true? debug-print) (print-message recvd :in))
    (json/read-str recvd :key-fn keyword)))


(defn send-json
  "Send a map as JSON over the ZMQ socket handle socket. 
  If :debug-print is true, will print what's being sent."
  [socket msg & {:keys [debug-print]}]
  (if (true? debug-print) (print-message msg :out))
  (let [outgoing-json (json/write-str msg)]
    (.send socket outgoing-json)
    ))

(defn start-communication
  "Connect to minichess framework on port port. You will be 
  registered with name my-name. Will continually loop, 
  responding to the requests of the minichess framework 
  and calling the appropriate function."
  [port my-name & {:keys [debug-print out]}]
      (reset! continue? true)
      (connect port)
      (reset! me my-name)
      (while continue?
        (let [recvd (get-next-json 
                     @socket-handle 
                     :debug-print debug-print)]
          (case (:strFunction recvd)
            "ping" (send-json @socket-handle 
                              {:strOut @me} :debug-print debug-print)

            "chess_reset" (do 
                            (board/reset) 
                            (send-json @socket-handle 
                                       {}
                                       :debug-print debug-print))

            "chess_boardGet" (send-json @socket-handle
                                        {:strOut (board/get-board)}
                                        :debug-print debug-print)

            "chess_winner" (send-json @socket-handle
                                      {:strReturn (board/winner)}
                                      :debug-print debug-print)

            "chess_moves" (let [moves 
                                (board/get-all-moves)]
                            (send-json @socket-handle
                                       {:intOut (count moves)
                                        :strOut (join moves)}
                                        :debug-print debug-print))

            "chess_boardSet" (send-json @socket-handle 
                                        {}
                                        :debug-print debug-print)

            "chess_isValid" (send-json @socket-handle
                                       {:boolReturn 
                                        (board/is-valid
                                         (:intX recvd)
                                         (:intY recvd))}
                                       :debug-print debug-print)

            "chess_isOwn" (send-json @socket-handle
                                     {:boolReturn
                                      (board/is-own
                                       (:strPiece recvd))}
                                     :debug-print debug-print)

            "chess_isEnemy" (send-json @socket-handle
                                     {:boolReturn
                                      (board/is-own
                                       (:strPiece recvd))}
                                     :debug-print debug-print)

            "chess_isNothing" (send-json @socket-handle
                                       {:boolReturn
                                      (board/is-own
                                       (:strPiece recvd))}
                                     :debug-print debug-print)

            "chess_eval" (send-json @socket-handle
                                    {:intReturn 
                                     (board/eval-board)}
                                    :debug-print debug-print)

            "chess_movesShuffled" (let [moves 
                                         (board/get-all-moves-shuffled)]
                                     (send-json @socket-handle
                                               {:intOut (count moves)
                                                :strOut (join moves)}
                                               :debug-print debug-print))

            "chess_movesEvaluated" (let [moves 
                                         (board/get-all-moves-evaluated)]
                                     (send-json @socket-handle
                                               {:intOut (count moves)
                                                :strOut (join moves)}
                                               :debug-print debug-print))
            
            "chess_move"  (do 
                            (board/move (:strIn recvd))
                             (send-json @socket-handle 
                                        {}
                                        :debug-print debug-print))

            "chess_moveRandom" (send-json @socket-handle 
                                          {:strOut (board/move-random)}
                                          :debug-print debug-print)
            
            "chess_moveGreedy" (send-json @socket-handle 
                                          {:strOut (board/move-greedy)}
                                          :debug-print debug-print)

            "chess_moveNegamax" (send-json @socket-handle 
                                          {:strOut (board/move-negamax
                                                    (:intDepth recvd)
                                                    (:intDuration recvd))}
                                          :debug-print debug-print)
            
            "chess_moveAlphabeta" (send-json @socket-handle 
                                          {:strOut (board/move-alphabeta
                                                    (:intDepth recvd)
                                                    (:intDuration recvd))}
                                          :debug-print debug-print)

            "chess_undo" (do 
                           (board/undo)
                           (send-json @socket-handle 
                                      {}
                                      :debug-print debug-print))

            )))
      (close-socket))

(comment
  ;; this example code will start the client
  (future
      (start-communication 54361 "Doug" :debug-print true)
      )
  (close-socket)
)
