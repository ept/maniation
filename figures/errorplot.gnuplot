set terminal pslatex
set output "errorplot.tex"
set logscale xy
set size ratio -0.2
#set xrange [1e-5:1e-1]
#set yrange [1e-15:1e6]
plot "errorplot" using 1:2 with linespoints title "error per step", \
    "errorplot" using 1:3 with linespoints title "error over 8 sec"
