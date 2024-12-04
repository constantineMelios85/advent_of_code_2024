package main

import (
	"fmt"
	"project/utils"
)

var directions = [][2]int{
	{0, 1},  // right
	{0, -1}, // left
	{1, 0},  // down
	{-1, 0}, // up
	{1, 1},  // down-right
	{1, -1}, // down-left
	{-1, 1}, // up-right
	{-1, -1}, // up-left
}

func main() {
	lines, err := utils.ReadFileLines("input.txt")
	if err != nil {
		fmt.Println("Error reading file:", err)
		return
	}

	matrix := createMatrix(lines)

	numberOccurrences := countWordOccurrences(matrix, directions, "XMAS")
	fmt.Println(numberOccurrences)

	crossedOccurrences := countDiagonalOccurrences(matrix, "MAS")
	fmt.Println(crossedOccurrences)

	utils.MeasureExecutionTime(func() {
		countWordOccurrences(matrix, directions, "XMAS")
	}, 10000, "First Solution")
	utils.MeasureExecutionTime(func() {
		countDiagonalOccurrences(matrix, "MAS")
	},
	 10000, "Second Solution")
}


func createMatrix(lines []string) [][]rune {
	matrix := make([][]rune, len(lines))
	for i, line := range lines {
		matrix[i] = []rune(line)
	}
	return matrix
}

func countWordOccurrences(matrix [][]rune, directions [][2]int, word string) int {
	rows, cols := len(matrix), len(matrix[0])
	wordRunes := []rune(word)
	count := 0

	for x := 0; x < rows; x++ {
		for y := 0; y < cols; y++ {
			for _, dir := range directions {
				if findWordAt(matrix, x, y, dir, wordRunes) {
					count++
				}
			}
		}
	}
	return count
}

func findWordAt(matrix [][]rune, x, y int, dir [2]int, word []rune) bool {
	rows, cols := len(matrix), len(matrix[0])

	for i := 0; i < len(word); i++ {
		nx, ny := x+i*dir[0], y+i*dir[1]
		if nx < 0 || ny < 0 || nx >= rows || ny >= cols || matrix[nx][ny] != word[i] {
			return false
		}
	}
	return true
}

func countDiagonalOccurrences(matrix [][]rune, word string) int {
	rows, cols := len(matrix), len(matrix[0])
	wordRunes := []rune(word)
	var middleChars [][2]int

	diagonalDirections := [][2]int{
		{-1, 1},  // top-right
		{1, 1},   // bottom-right
		{-1, -1}, // top-left
		{1, -1},  // bottom-left
	}

	for x := 0; x < rows; x++ {
		for y := 0; y < cols; y++ {
			for _, dir := range diagonalDirections {
				if findWordAt(matrix, x, y, dir, wordRunes) {
					middleChars = append(middleChars, [2]int{x + dir[0], y + dir[1]})
				}
			}
		}
	}

	return countDoublePairs(middleChars)
}


func countDoublePairs(pairs [][2]int) int {
	countMap := make(map[[2]int]int)

	for _, pair := range pairs {
		countMap[pair]++
	}

	count := 0
	for _, freq := range countMap {
		if freq == 2 {
			count++
		}
	}

	return count
}
