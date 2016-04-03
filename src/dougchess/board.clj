;; Henry Cooney <hacoo36@gmail.com> <Github: hacoo>
;; 1 Apr. 2016 :)
;; dougchess/board

;; Basic functions for dealing with the board, moving, etc.

;; The board is represented as a nested vector.
(ns dougchess.board
  (:require [clojure.string :refer [join]])
)



;; Global board state:
(def turn (atom nil)) ;; the turn number
(def color (atom nil)) ;; who is playing
(def board (atom nil)) ;; the current board state

(defn board-to-string
  "String -> String
  Compress the board into a single string and return."
  [board]
  (let [splits (partition 5 board)]
    (str @turn " " @color "\n"
         (join "\n" (map #(apply str %) splits))
         "\n"
         )))

(defn is-valid
 "int -> int -> bool
  Check whether a given (X, Y) position is valid"
  [x y]
  (and (<= x 4) (>= x 0) (<= y 5) (>= y 0)))

(defn is-own
  "String -> bool
  Determine whether a given piece is mine."
  [piece]
  false)

(defn is-enemy
  "String -> bool
  Determine whether a given piece is the opponents."
  [piece]
  false)

(defn is-nothing
  "String -> bool
  Determine whether a given piece is an empty square."
  [piece]
  false)

(defn reset
  "reset turn! reset color!
  Reset the current chess board state. Note that nothing
  is returned."
  []
  (reset! color "W")
  (reset! turn 1)
  (reset! board "kqbrnppppp..........PPPPPRNBQK")
  )

(defn winner
  "Return the current winner, or ? if the game isn't done"
  []
  (str "?") ;; 63\0
  )

(defn get-board
  "Return the current state of the board as a single string."
  []
  (board-to-string @board))

(defn get-all-moves
  "String -> [String]
  Return all possible moves for the current board, each as a string"
  []
  ["a5-a4\n"
   "b5-b4\n"
   "c5-c4\n"
   "e5-e4\n"
   "b6-a4\n"
   "b6-c4\n"])

(defn get-all-moves-shuffled
  "-> [String]
  Return all possible moves, shuffled to a random order"
  []
  [])


(defn get-all-moves-evaluated
  "-> [String]
  Return all possible moves, ordered by best evaluation"
  []
  [])


(defn eval-board
  " -> int
  Evaluate the current cost of the board"
  []
  0)

(defn move
  "String -> update board!
  Move as directed by the input."
  [m]
  nil)

(defn move-random
  "-> String update board!
  Make a random move and return it."
  []
  "a5-a4\n")

(defn move-greedy
 "-> String update board!
  Make a greedy move and return it."
  []
  "a5-a4\n")

(defn move-negamax
  "int -> int -> String update board!
  Make a negamax move and return it."
  [depth duration]
  "a5-a4\n")

(defn move-alphabeta
  "int -> int -> String update board!
  Make an alphabeta move and return it."
  [depth duration]
  "a5-a4\n")

(defn undo
  "-> update board!
  Undo the last move."
  []
  nil)

