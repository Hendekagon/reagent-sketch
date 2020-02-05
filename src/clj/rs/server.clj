(ns rs.server
  (:require
    [clojure.pprint :as pprint]
    [clojure.stacktrace :as st]
    [clojure.core.async :refer [<! >! put! chan go]]
    [clojure.string :as string]
    [cognitect.transit :as transit]
    [aleph.http :as http]
    [aleph.http.client :as http-client]
    [ring.middleware.file :as rf]
    [ring.middleware.defaults :as rmd]
    [ring.util.response :as rr]
    [ring.util.io :as rio]
    [compojure.route :refer [files not-found]]
    [compojure.handler :refer [site]]
    [compojure.core :refer [routes GET POST DELETE ANY context]]
    [hiccup.core :as hicp]
    [manifold.bus :as bus]
    [manifold.deferred :as md]
    [manifold.stream :as ms]
    [byte-streams :as bs]
    [rs.style :as css]
    [garden.core :as gc]))

(defn make-state
  ([] {}))

(defn page [req]
  {:status  200
   :headers {"content-type" "text/html"}
   :body
    (hicp/html
      [:html [:head {} [:title "RS"] [:meta {:charset :UTF-8}]]
       [:body
        [:div#app
         [:script {:src "js/main.js" :type "text/javascript"}]]]])})

(defmulti messages
  (fn [messaging message]
    (:request message)))

(defmethod messages :default
  [messaging message]
  (println "default:" message))

(def non-websocket-request
  {:status  400
   :headers {"content-type" "application/text"}
   :body    "Expected a websocket request."})

(defn add-messaging! [{{b :broadcast} :messaging :as state}]
  (when b (ms/close! b))
  (-> state
    (assoc-in [:messaging :broadcast] (ms/stream))))

(defn ws-handler
  [{messaging :messaging :as state} {{ws :ws :as session} :session :as req}]
  (->
    (md/let-flow [stream (http/websocket-connection req {:max-frame-payload (* 65536 4096)})
                  pstream (ms/stream)]
      (ms/connect (->> (:broadcast messaging) (ms/map pr-str)) stream)
      (ms/connect (ms/map pr-str pstream) stream)
      (ms/consume
        (fn [m]
          (try
            (messages (assoc messaging :client-stream stream
                         :pr-client-stream pstream :request req) m)
            (catch Exception e (println e))))
        (->> stream (ms/map read-string) (ms/filter :request)))
      {:status 200 :session (assoc session :ws stream)})
    (md/catch
      (fn [_]
        non-websocket-request))))

(defn add-routes!
  [state]
  (assoc state :routes
    (->
      (routes
        (GET "/" [] page)
        (GET "/ws" [] (partial ws-handler state))
        (files "/" {:root "resources/public"})
        (not-found "404"))
      (rmd/wrap-defaults rmd/site-defaults))))

(defn add-server! [{config :config routes :routes :as state}]
  (assoc state :server
    (http/start-server routes config)))

(defn start!
  ([]
    (start! (atom {})))
  ([state]
   (start! {:port 8081} state))
  ([config state]
   (when (:server @state)
     (.close (:server @state)))
   (reset! state
     (->
       (make-state)
       (assoc :config config)
       add-messaging!
       add-routes!
       add-server!
       ))
   state))

(defn update-css! [state]
  (require :reload 'rs.style)
  (ms/put! (get-in state [:messaging :broadcast])
    {:topic :server-event :update :css :params {:path [:css] :value (css/css-rules {})}}))

(defn update-fn! [state]
  (ms/put! (get-in state [:messaging :broadcast])
    {:topic :server-event :kind :assoc-in* :event {:path [:fns :test] :item '(fn [x y] (list x y))}}))

(defn -main []
  (println "starting...")
  (start!))