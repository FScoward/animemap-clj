(ns animemap-clj.core
  (:require [clojure.data.json :as json]
            [postal.core :as postal]))

(def anime (slurp "http://animemap.net/api/table/tokyo.json"))
(def anime-map (:item (:response (json/read-str anime :key-fn keyword))))
(def today (filter #(= (:today %) "1") anime-map))

(defn make-email-txt [source]
  (str "[title] " (:title source)
       " [time] " (:time source)
       "\n"))

(defn send-email [title txt]
  (postal/send-message {:host "smtp.gmail.com"
                 :user "<username>"
                 :pass "<password>"
                 :ssl :yes}
                {:from "<from address>"
                 :to "<to address>"
                 :subject "test"
                 :body txt})
  {:code 0, :error :SUCCESS, :message "message sent"})

;(map #(str "title: " (:title %) " time: " (:time %) " station: " (:station %)) today)
(def body (map #(make-email-txt %) today))
(reduce str body)

(defn -main []
  (send-email "今日のアニメ" (apply str body)))
