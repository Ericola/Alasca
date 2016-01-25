set title "Variations du temps moyen d'execution d'une requete au cours du temps"
set xlabel "Numero des moyennes recues"
set ylabel "Temps moyen d'execution d'une requete (ms)"
plot "acc1Courbe.txt" using 1:2 title 'Temps moyen' with linespoints, \
"acc1Courbe.txt" using 1:3 title 'Adaptation'

