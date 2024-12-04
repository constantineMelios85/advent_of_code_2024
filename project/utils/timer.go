package utils

import (
	"fmt"
	"strings"
	"time"
)


func MeasureExecutionTime(fn func(), iterations int, message string) {
	if iterations <= 0 {
		iterations = 1000 
	}
	if message == "" {
		message = "Execution" 
	}

	
	startTime := time.Now()
	for i := 0; i < iterations; i++ {
		fn() 
	}
	totalTime := time.Since(startTime).Nanoseconds() 

	
	averageTime := totalTime / int64(iterations)
	formattedTime := FormatWithThousandSeparators(averageTime)


	fmt.Printf("%s - Average time taken for %d iterations: %s ns\n", message, iterations, formattedTime)
}

func FormatWithThousandSeparators(n int64) string {
	numStr := fmt.Sprintf("%d", n) 
	var result strings.Builder
	numLen := len(numStr)

	
	for i, ch := range numStr {
		if i > 0 && (numLen-i)%3 == 0 {
			result.WriteString(" ") 
		}
		result.WriteRune(ch)
	}
	return result.String()
}