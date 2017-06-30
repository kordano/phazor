(ns phazor.core
  (:require [cljsjs.phaser]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce demo (atom {:circle {}
                     :nodes []
                     :edges []
                     :note "foobar"
                     :game   {}}))

(defn create-circle [state position-x position-y size]
  (swap! state update :nodes conj (js/Phaser.Circle. position-x position-y size)))

(defn connect-circles [state circle-0 circle-1]
  (swap! state update :edges conj (js/Phaser.Line. (.-x circle-0) (.-y circle-0) (.-x circle-1) (.-y circle-1))))

(defn create []
  (create-circle demo (.. (get @demo :game) -world -centerX) 100 32)
  (create-circle demo 50 (.. (get @demo :game) -world -centerY) 32))

(defn render []
  (let [{:keys [game nodes edges note]} (deref demo)
        world (.-world game)]
    (doall
     (concat
      [(.. game -debug (text note (.-centerX world) (.-centerY world)))]
      (map #(.. game -debug (geom % "#cf00ff")) nodes)
      (map #(.. game -debug (geom % "#ff0000")) edges)))))

(defn update-game []
  (let [{:keys [game]} (deref demo)
        position (.. game -input -activePointer -position)]))

(defn create-game []
  (swap! demo assoc :game (js/Phaser.Game. 300 300 js/Phaser.CANVAS "app" #js {:create create :render render :update update-game})))

(defn restart-game []
  (let [{:keys [game]} (deref demo)]
    (.destroy game)
    (create-game)))


(comment

  (create-game)

  (clj->js update-game)

  (create-circle demo 40 40 10)

  (let [[c0 c1] (take-last 2 (:nodes @demo))]
    (connect-circles demo c0 c1))

  (count (:edges @demo))


  (swap! demo assoc :note "blö")

  (-> demo deref :game)


  )

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (restart-game)
)
