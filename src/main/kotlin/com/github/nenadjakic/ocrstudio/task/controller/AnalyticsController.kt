package com.github.nenadjakic.ocrstudio.task.controller

import com.github.nenadjakic.ocrstudio.task.dto.StatusCount
import com.github.nenadjakic.ocrstudio.task.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Analytics controller", description = "API endpoints for task analytics.")
@RestController
@RequestMapping("/api/v1/task/analytic")
class AnalyticsController(
    private val taskService: TaskService
) {

    /**
     * Retrieves the count of tasks grouped by their status.
     *
     * @return a [ResponseEntity] containing a list of [StatusCount] objects, each representing a status and its corresponding count.
     * The response entity has an HTTP status of 200 (OK) if the operation is successful.
     */
    @Operation(
        operationId = "getCountByStatus",
        summary = "Get count by status.",
        description = "Returns count by status information.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved count by status.")
        ]
    )
    @GetMapping(value = ["/count-by-status"], produces = [org.springframework.http.MediaType.APPLICATION_JSON_VALUE])
    fun getCountByStatus(): ResponseEntity<List<StatusCount>> = ResponseEntity.ok(taskService.getCountByStatus())

    /**
     * Retrieves the average count across inDocuments.
     *
     * @return a [ResponseEntity] containing the average count as a Long.
     * The response entity has an HTTP status of 200 (OK) if the operation is successful.
     */
    @Operation(
        operationId = "getAverageInDocuments",
        summary = "Get average count across inDocuments.",
        description = "Returns average count across inDocuments.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved average count across inDocuments.")
        ]
    )
    @GetMapping(value = ["/average-in-documents"], produces = [org.springframework.http.MediaType.APPLICATION_JSON_VALUE])
    fun getAverageInDocuments(): ResponseEntity<Long> = ResponseEntity.ok(taskService.getAverageInDocuments())
}