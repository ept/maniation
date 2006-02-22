.SUFFIXES = .tex .bib .aux .bbl .dvi .ps .pdf

all:	diss.pdf
	@echo ''
	@echo -n 'Word count: '
	@$(HOME)/tools/detex/detex -e appendix,equation,eqnarray,eqnarray* diss.tex | wc -w

diss.pdf:	diss.ps
	gs -sDEVICE=pdfwrite -sOutputFile=diss.pdf -dBATCH -dNOPAUSE diss.ps

diss.ps:	diss.dvi
	dvips -Ppdf diss.dvi

diss.dvi:	diss.bbl
	latex diss
	latex diss

diss.bbl:	diss.bib diss.aux
	bibtex diss

diss.aux:	*.tex
	latex diss

clean:
	rm -f *.{log,aux,bbl,blg,dvi,ps}

veryclean:	clean
	rm -f diss.pdf
