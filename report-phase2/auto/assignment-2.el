(TeX-add-style-hook "assignment-2"
 (lambda ()
    (LaTeX-add-labels
     "sec:elected_reengineerings")
    (TeX-run-style-hooks
     "biblatex"
     "graphicx"
     "fancyvrb"
     "wrapfig"
     "multirow"
     "enumerate"
     "hyperref"
     "inputenc"
     "utf8"
     "latex2e"
     "art10"
     "article"
     "a4paper"
     "10pt")))

