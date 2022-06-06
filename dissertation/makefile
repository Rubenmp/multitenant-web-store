build:
	pdflatex main.tex
	biber main
	pdflatex main.tex
	pdflatex main.tex

clean:
	# Remove base latex files
	rm -f *.aux *.log *.out *.toc *.synctex.gz

	# Remove bibliography auxiliary files
	rm -f *.aux.bbl *.aux.blg *.bbl *.bcf *.blg *.run.xml
