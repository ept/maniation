set terminal pslatex
set output "errorplot.tex"
set logscale xy
set size ratio -0.2
set key left Left reverse height 1 spacing 2
set xlabel "time step length"
set ylabel "error"
set yrange [1e-17 : 1e2]
plot "errorplot" using 1:2 with linespoints title "error per step", \
    "errorplot" using 1:3 with linespoints title "error over 8 sec"
