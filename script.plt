set title "Variations du temps moyen d'�x�cution d'une requ�te au cours du temps"
set xlabel "Num�ro des moyennes re�ues"
set ylabel "Temps moyen d'�x�cution d'une requ�te (ms)"
plot "Courbe.txt" using 1:2 title 'Temps moyen' with linespoints 

