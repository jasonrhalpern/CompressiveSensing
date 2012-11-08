# basic tools
LATEX   = latex
BIBTEX  = bibtex
DVIPS   = dvips
PS2PDF  = ps2pdf

# sources
BASE	= main
TEX_SRC = $(BASE).tex
BIB_SRC = *.bib

TARGET = midterm_report.pdf

# default target
all: $(TARGET)

# build
$(TARGET): $(TEX_SRC) $(BIB_SRC)
	# standard boilerplate (latex; bibtex; latex; latex)
	$(LATEX) $(TEX_SRC)
	$(BIBTEX) *.aux
	$(LATEX) $(TEX_SRC)
	$(LATEX) $(TEX_SRC)

	# dvi to ps
	$(DVIPS) -Ppdf *.dvi

	# ps to pdf
	$(PS2PDF) *.ps $(TARGET)

	# cleanup
	rm -f *.aux *.bbl *.blg *.dvi *.log *.ps *.out

# clean
clean:
	/bin/rm -f *.pdf *.dvi *.aux *.ps *~ *.log
	/bin/rm -f *.lot *.lof *.toc *.blg *.bbl url.sty *.out
	/bin/rm -f *.bak

