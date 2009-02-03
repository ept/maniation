set terminal pslatex
set output "cradle.tex"
set origin 0,0
set size 0.9,2.6
set multiplot
set key off
set tics out
set grid xtics ytics
set xtics nomirror ("" 0, "" 0.1438, "" 0.4316, "" 0.7194, "" 1.007, "" 1.294)
set xzeroaxis
set lmargin 2.5
set rmargin 0
set tmargin 0
set bmargin 0
set size 0.42,0.25
set origin 0.09,2.3
set ylabel "Ball 1\n\nangle / deg"
set yrange [-5 : 30]
set ytics nomirror ("" -5 1, "0" 0 0, "" 5 1, "10" 10 0, "" 15 1, "20" 20 0, "" 25 1, "30" 30 0)
plot "elastic" using 1:2 with lines notitle
set origin 0.09,2.0
set ylabel "Ball 2\n\nangle / deg"
set yrange [-15 : 15]
set ytics nomirror ("" -15 1, "-10" -10 0, "" -5 1, "0" 0 0, "" 5 1, "10" 10 0, "" 15 1)
plot "elastic" using 1:3 with lines notitle
set origin 0.09,1.7
set ylabel "Ball 5\n\nangle / deg"
set yrange [-30 : 5]
set ytics nomirror ("-30" -30 0, "" -25 1, "-20" -20 0, "" -15 1, "-10" -10 0, "" -5 1, "0" 0 0, "" 5 1)
plot "elastic" using 1:4 with lines notitle
set origin 0.09,1.4
set ylabel "Ball 1\nmomentum\n\n$/\\; 10^{-4}\\frac{\\mathrm{kg}\\,\\mathrm{m}}{\\mathrm{s}}$"
set yrange [-3.5e-4:3.5e-4]
set ytics nomirror ("" -3e-4 1, "-2" -2e-4 0, "" -1e-4 1, "0" 0 0, "" 1e-4 1, "2" 2e-4 0, "" 3e-4 1)
plot "elastic" using 1:5 with lines notitle
set origin 0.09,1.1
set ylabel "Ball 2\nmomentum\n\n$/\\; 10^{-4}\\frac{\\mathrm{kg}\\,\\mathrm{m}}{\\mathrm{s}}$"
plot "elastic" using 1:6 with lines notitle
set origin 0.09,0.8
set ylabel "Ball 5\nmomentum\n\n$/\\; 10^{-4}\\frac{\\mathrm{kg}\\,\\mathrm{m}}{\\mathrm{s}}$"
plot "elastic" using 1:7 with lines notitle
set origin 0.09,0.5
set ylabel "Energy / J"
set yrange [0 : 0.002]
set ytics ("0" 0, "0.0005" 0.0005, "0.0010" 0.001, "0.0015" 0.0015, "0.0020" 0.002)
plot "elastic" using 1:8 with lines notitle
set origin 0.09,0.2
set xlabel "Time / s"
set xtics ("0" 0, "0.14" 0.1438, "0.43" 0.4316, "0.72" 0.7194, "1.00" 1.007, "1.29" 1.294)
set ylabel "Step size / s"
set yrange [0 : 0.014]
set ytics ("0" 0 0, "" 0.002 1, "" 0.004 1, "" 0.006 1, "" 0.008 1, "0.01" 0.01 0, "" 0.012 1, "" 0.014 1)
plot "elastic" using 1:9 with lines notitle
set origin 0.51,2.3
set xlabel ""
set xtics nomirror ("" 0, "" 0.1438, "" 0.4308, "" 0.7171, "" 1.003, "" 1.289)
set ylabel ""
set yrange [-5 : 30]
set ytics mirror ("" -5 1, "" 0 0, "" 5 1, "" 10 0, "" 15 1, "" 20 0, "" 25 1, "" 30 0)
plot "inelastic" using 1:2 with lines notitle
set origin 0.51,2.0
set yrange [-15 : 15]
set ytics mirror ("" -15 1, "" -10 0, "" -5 1, "" 0 0, "" 5 1, "" 10 0, "" 15 1)
plot "inelastic" using 1:3 with lines notitle
set origin 0.51,1.7
set yrange [-30 : 5]
set ytics mirror ("" -30 0, "" -25 1, "" -20 0, "" -15 1, "" -10 0, "" -5 1, "" 0 0, "" 5 1)
plot "inelastic" using 1:4 with lines notitle
set origin 0.51,1.4
set yrange [-3.5e-4:3.5e-4]
set ytics mirror ("" -3e-4 1, "" -2e-4 0, "" -1e-4 1, "" 0 0, "" 1e-4 1, "" 2e-4 0, "" 3e-4 1)
plot "inelastic" using 1:5 with lines notitle
set origin 0.51,1.1
plot "inelastic" using 1:6 with lines notitle
set origin 0.51,0.8
plot "inelastic" using 1:7 with lines notitle
set origin 0.51,0.5
set yrange [0 : 0.002]
set ytics ("" 0, "" 0.0005, "" 0.001, "" 0.0015, "" 0.002)
plot "inelastic" using 1:8 with lines notitle
set origin 0.51,0.2
set xlabel "Time / s"
set xtics ("0" 0, "0.14" 0.1438, "0.43" 0.4308, "0.72" 0.7171, "1.00" 1.003, "1.29" 1.289)
set yrange [0 : 0.014]
set ytics ("" 0 0, "" 0.002 1, "" 0.004 1, "" 0.006 1, "" 0.008 1, "" 0.01 0, "" 0.012 1, "" 0.014 1)
plot "inelastic" using 1:9 with lines notitle
unset multiplot
