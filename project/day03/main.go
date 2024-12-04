package main

import (
	"fmt"
	"project/utils"
	"regexp"
	"strconv"
	"strings"
)

const (
	allowInstruction    = "do()"
	disallowInstruction = "don't()"
)

func main() {
	lines, err := utils.ReadFileLines("input.txt")
	if err != nil {
		fmt.Println("Error reading file:", err)
		return
	}

	input := strings.Join(lines, "")

	multiplications := findRegexMatches(input, `mul\(\d+,\d+\)`)
	sum := sumMultiplicationList(multiplications)
	fmt.Println(sum)

	instructions := findRegexMatches(input, `mul\(\d+,\d+\)|do\(\)|don't\(\)`)
	validMultiplications := filterValidMultiplications(instructions)
	conditionalSum := sumMultiplicationList(validMultiplications)
	fmt.Println(conditionalSum)

	utils.MeasureExecutionTime(func() {
		multiplications := findRegexMatches(input, `mul\(\d+,\d+\)`)
	  sumMultiplicationList(multiplications)
	}, 10000, "First Solution")

	utils.MeasureExecutionTime(func() {
		instructions := findRegexMatches(input, `mul\(\d+,\d+\)|do\(\)|don't\(\)`)
		validMultiplications := filterValidMultiplications(instructions)
		sumMultiplicationList(validMultiplications)
	}, 10000, "Second Solution")
}

func sumMultiplicationList (multiplications []string) int {
	var sum int
	for _, multiplication := range multiplications {
		sum += calculateMultiplication(multiplication)
	}
	return sum
}

func findRegexMatches(input, pattern string) []string {
	re := regexp.MustCompile(pattern)
	return re.FindAllString(input, -1)
}

func calculateMultiplication(expression string) int {
	re := regexp.MustCompile(`\d+`)
	matches := re.FindAllString(expression, -1)
	if len(matches) < 2 {
		fmt.Printf("Invalid multiplication format: %s\n", expression)
		return 0
	}

	a, err := strconv.Atoi(matches[0])
	if err != nil {
		fmt.Printf("Error converting %s to integer\n", matches[0])
		return 0
	}

	b, err := strconv.Atoi(matches[1])
	if err != nil {
		fmt.Printf("Error converting %s to integer\n", matches[1])
		return 0
	}

	return a * b
}

func filterValidMultiplications(instructions []string) []string {
	var (
		multiplications   []string
		allowMultiplication = true
	)

	for _, instruction := range instructions {
		switch instruction {
		case allowInstruction:
			allowMultiplication = true
		case disallowInstruction:
			allowMultiplication = false
		default:
			if allowMultiplication {
				multiplications = append(multiplications, instruction)
			}
		}
	}

	return multiplications
}
