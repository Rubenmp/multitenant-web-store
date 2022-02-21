build:
	pdflatex dissertation.tex
	biber dissertation
	pdflatex dissertation.tex
	pdflatex dissertation.tex

clean:
	# Remove base latex files
	rm -f *.aux *.log *.out *.toc

	# Remove bibliography auxiliary files
	rm -f *.aux.bbl *.aux.blg *.bbl *.bcf *.blg *.run.xml
