set title "Variations du temps moyen d'éxécution d'une requête au cours du temps"
set xlabel "Numéro des moyennes reçues"
set ylabel "Temps moyen d'éxécution d'une requête (ms)"
plot "Courbe.txt" using 1:2 title 'Temps moyen' with linespoints 

