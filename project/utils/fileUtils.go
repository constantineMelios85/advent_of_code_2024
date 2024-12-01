package utils

import (
	"bufio"
	"fmt"
	"os"
)

func ReadFileLines(filename string) ([]string, error) {
	file, err := os.Open(filename)
	if err != nil {
		return nil, err
	}
	defer file.Close()

	var lines []string
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		lines = append(lines, scanner.Text())
	}

	if err := scanner.Err(); err != nil {
		return nil, err
	}
	return lines, nil
}

func main() {
	// Example usage
	lines, err := ReadFileLines("input.txt")
	if err != nil {
		fmt.Println("Error reading file:", err)
		return
	}

	for _, line := range lines {
		fmt.Println(line)
	}
}
