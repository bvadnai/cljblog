(ns cljblog.admin)

(def admin-login (or (System/getenv "CLJBLOG_ADMIN_LOOGIN")
                     "adminka"))
(def admin-passw (or (System/getenv "CLJBLOG_ADMIN_PASSW")
                     "micimacko"))

(defn check-login [login passw]
  (and (= login admin-login)
       (= passw admin-passw)))