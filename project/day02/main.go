package main

import (
	"fmt"
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

	var safeLists int
	for _, line := range lines {
		if isListSafe(parseLine(line)) {
			safeLists++
		}
	}

	fmt.Println(safeLists)

	var safeListsWithDampener int
	for _, line := range lines {
		if isListSafeWithDampener(parseLine(line)) {
			safeListsWithDampener++
		}
	}
	fmt.Println(safeListsWithDampener)

	utils.MeasureExecutionTime(func() {
		for _, line := range lines {
		if isListSafe(parseLine(line)) {
			safeLists++
		}
	}
	}, 10000, "First Solution")
	utils.MeasureExecutionTime(func() {
		for _, line := range lines {
		if isListSafeWithDampener(parseLine(line)) {
			safeListsWithDampener++
		}
	}
	}, 10000, "Second Solution")
}

func parseLine(line string) ([]int) {
    strValues := strings.Split(line, " ")
    intValues := make([]int, len(strValues))
    for i, str := range strValues {
        intValue, err := strconv.Atoi(str)
        if err != nil {
            return nil
        }
        intValues[i] = intValue
    }
    return intValues
}

func isListSafe (list []int)  bool {
	var differences = sortList(diffs(list))

	return isSafe(differences)
}

func isListSafeWithDampener(list []int) bool {
    if isListSafe(list) {
        return true
    }

    for i := 0; i < len(list); i++ {
        modifiedList := append([]int{}, list[:i]...) 
        modifiedList = append(modifiedList, list[i+1:]...)
        if isListSafe(modifiedList) {
            return true
        }
    }

    return false
}

func isSafe(differences []int) bool {
    first := differences[0]
    last := differences[len(differences)-1]

    if (first < 0 && last < 0 && first >= -3 && last >= -3) || 
       (first > 0 && last > 0 && first <= 3 && last <= 3) {
        return true
    }

    return false
}

func diffs (list []int) []int {
	var differences []int
	for i := 1; i < len(list); i++ {
		differences = append(differences, list[i] - list[i-1])
	}

	return differences
}

func sortList (list []int) []int {
	sort.Ints(list)
	return list
}
