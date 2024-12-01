package main

import (
	"fmt"
	"math"
	"project/utils"
	"sort"
	"strconv"
	"strings"
)

func main() {
	lines, err := utils.ReadFileLines("input.txt")
	if err != nil {
		fmt.Println("Error reading file:", err)
		return
	}

	leftNumbers, rightNumbers := parseAndFilterNumberPairs(lines)

	// Diff
	sort.Ints(leftNumbers)
	sort.Ints(rightNumbers)

	diff := calculateDifferenceSum(leftNumbers, rightNumbers)

	fmt.Println(diff)

	//Similarity
	similarity := sumOccurrences(leftNumbers, rightNumbers)
	fmt.Println(similarity)
}


func parseAndFilterNumberPairs(lines []string) ([]int, []int) {
	var leftNumbers, rightNumbers []int
	for _, line := range lines {
		parts := strings.Fields(line)
		if len(parts) < 2 {
			continue
		}

		left, err1 := strconv.Atoi(parts[0])
		right, err2 := strconv.Atoi(parts[1])
		if err1 != nil || err2 != nil {
			continue
		}

		leftNumbers = append(leftNumbers, left)
		rightNumbers = append(rightNumbers, right)
	}
	return leftNumbers, rightNumbers
}


func calculateDifferenceSum(leftNumbers, rightNumbers []int) int {
	var sum int
	for i := range leftNumbers {
		sum += int(math.Abs(float64(leftNumbers[i] - rightNumbers[i])))
	}
	return sum
}

func sumOccurrences(list1, list2 []int) int {
	occurrencesMap := occurrencesMap(list2)
	
	totalSum := 0
	for _, num := range list1 {
		count := occurrencesMap[num]
		totalSum += count * num
	}

	return totalSum
}

func occurrencesMap(list []int) map[int]int {
	occurrences := make(map[int]int)
	for _, num := range list {
		occurrences[num]++
	}
	return occurrences
}