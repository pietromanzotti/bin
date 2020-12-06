#!/usr/bin/env boot

(set-env!
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [slf4j-timbre "0.1.0-SNAPSHOT"]
                 [irresponsible/tentacles "0.6.6"]
                 [clj-commons/conch "0.9.2"]])

(require '[tentacles.repos :as repos])
(require '[me.raynes.conch :refer [programs with-programs let-programs] :as sh])
(require '[boot.cli :refer [defclifn]])


(defn get_json
  [user]
  (let [json (promise)]
    (deliver json (repos/user-repos (str user)))
    @json))

(defn get_urls
  [json]
  (let [urls (map :clone_url json)]
    urls))

(defn clone_url
  [url]
  (programs git)
  (print "Cloning " url "...")
  (git "clone" url)
  (println "[OK]"))

(defn clone_all
  [urls]
  (dorun(map #(clone_url %) urls))
  (println "Cloned all repository."))


(defclifn -main
  "Clona tutti i repository di un utente github 
  nella directory corrente. 

  USAGE: gitall [options] [user-account]"
  
  [u user USER str "il nome utente github"
   f force bool "Forza il comando senza confermare"]

  (if (nil? (:user *opts*))
    (println "ERRORE: devi inserire il nome utente github. Prova -h")
    (if (:force *opts*)
      (do
        (clone_all (get_urls (get_json user))))
      (do
        (programs pwd)
        (let [dir pwd]
          (println)
          (println (str "Are you sure to gitall in " (pwd) "?")))))))


