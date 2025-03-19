package main

import (
	"image/color"
	"math/rand"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/layout"
	"fyne.io/fyne/v2/widget"
)

func main() {
	myApp := app.New()
	window := myApp.NewWindow("Simple Window")
	window.Resize(fyne.NewSize(400, 300))

	label := widget.NewLabel("Press the button to change background color")
	label.Alignment = fyne.TextAlignCenter

	content := container.NewWithoutLayout(label)
	content.Layout = layout.NewMaxLayout()

	button := widget.NewButton("Change Color", func() {
		// Generate a random color
		r := uint8(rand.Intn(256))
		g := uint8(rand.Intn(256))
		b := uint8(rand.Intn(256))
		randomColor := color.RGBA{R: r, G: g, B: b, A: 255}

		// Set the background color
		content.BackgroundColor = randomColor
		content.Refresh()
	})

	mainContainer := container.NewBorder(label, button, nil, nil, content)
	window.SetContent(mainContainer)
	window.ShowAndRun()
}
