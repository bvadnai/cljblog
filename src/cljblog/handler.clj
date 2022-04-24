(ns cljblog.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as resp]
            [ring.middleware.session :as session]
            [cljblog.db :as db]
            [cljblog.pages :as p]
            [cljblog.admin :as a]))



(defroutes app-routes
           (GET "/" [] (p/index (db/list-articles)))

           (GET "/articles/:id" [id] (p/article (db/get-article-by-id id)))

           (GET "/admin/login" [:as {session :session}]
             (if (:admin session)
               (resp/redirect "/")
               (p/admin-login)))

           (POST "/admin/login" [login password]
             (if (a/check-login login password)
               (-> (resp/redirect "/")
                   (assoc-in [:session :admin] true))
               (p/admin-login "Wrong username or password!")))

           (GET "/admin/logout" []
             (-> (resp/redirect "/")
                 (assoc-in [:session :admin] false)))

           (route/not-found "Not Found"))

(defroutes admin-routes
           (GET "/articles/new" [] (p/edit-article nil))

           (POST "/articles" [title body]
             (do (db/create-article title body)
                 (resp/redirect "/")))

           (GET "/articles/:id/edit" [id] (p/edit-article (db/get-article-by-id id)))

           (POST "/articles/:id" [id title body]
             (do (db/update-article id title body)
                 (resp/redirect (str "/articles/" id))))

           (DELETE "/articles/:id" [id]
             (do (db/delete-article id)
                 (resp/redirect "/"))))

(defn wrap-admin-only [handler]
  (fn [request]
    (if (-> request :session :admin)
      (handler request)
      (resp/redirect "/admin/login"))))

(def app
  (-> (routes (wrap-routes admin-routes wrap-admin-only)
              app-routes)
      (wrap-defaults site-defaults)
      session/wrap-session))
