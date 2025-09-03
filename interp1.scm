;; -*- Mode: Scheme48; -*-                                                       ;;
;; interp1.scm
;;
;; AUTHORS: (CSC 417 Fall 2025) Jamie A. Jennings
;; Thursday, August 28, 2025

;; Environments

(define initial-env
  '((i 1)
    (v 5)
    (x 10)
    (l 50)
    (c 100)
    (d 500)
    (m 1000)))

(define lookup
  (lambda (key env)
    (let ((probe (assq key env)))
      (if probe
          ;; return second part of list here
          (cadr probe)
          (error "417 error: undefined identifier" key)))))
          

;; Eval

(define ev
  (lambda (exp)
    (cond ((number? exp)
           exp)
          ((string? exp)
           exp)
          ((symbol? exp)
           (lookup exp initial-env))
          (else
           (error "417 error: not a valid expression type"
                  exp)))))

;; What this little interpreter can do:

;; > (ev 'x)
;; 10
;; > (ev "Hi")
;; "Hi"
;; > (ev 1010)
;; 1010
;; > (ev +)
;;
;; error: 417 error: not a valid expression type
;;        #{procedure 110 +}
;; 1> 
;; > (ev 'k)
;;
;; error: 417 error: undefined identifier
;;        k
;; 1> 
;; > 
