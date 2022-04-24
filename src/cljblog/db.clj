(ns cljblog.db
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

(def db (-> "mongodb://127.0.0.1/cljblog-test"
            mg/connect-via-uri
            :db))
