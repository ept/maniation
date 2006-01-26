.SUFFIXES = .tex .bib .aux .bbl .pdf

all:	diss.pdf
	@echo ''
	@echo -n 'Word count: '
	@$(HOME)/tools/detex/detex -e appendix,equation,eqnarray,eqnarray* diss.tex | wc -w

diss.pdf:	diss.bbl
	pdflatex diss
	pdflatex diss

diss.bbl:	diss.bib diss.aux
	bibtex diss

diss.aux:	*.tex
	pdflatex diss

clean:
	rm -f *.{log,aux,bbl,blg}

veryclean:	clean
	rm -f diss.pdf
