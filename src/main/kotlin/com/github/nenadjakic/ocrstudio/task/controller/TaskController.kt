package com.github.nenadjakic.ocrstudio.task.controller

import com.github.nenadjakic.ocrstudio.task.dto.*
import com.github.nenadjakic.ocrstudio.task.entity.OcrConfig
import com.github.nenadjakic.ocrstudio.task.entity.SchedulerConfig
import com.github.nenadjakic.ocrstudio.task.entity.Task
import com.github.nenadjakic.ocrstudio.task.exception.MissingDocumentException
import com.github.nenadjakic.ocrstudio.task.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Encoding
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*


@Tag(name = "Task controller", description = "API endpoints for managing task entities.")
@RestController
@RequestMapping("/api/v1/task")
@Validated
class TaskController(
    private val taskService: TaskService,
    private val modelMapper: ModelMapper
) {
    /**
     * Retrieves all tasks.
     *
     * This endpoint returns a list of all tasks available in the system.
     * It responds with a 200 status code upon successful retrieval.
     *
     * @return [ResponseEntity] containing a list of Task objects.
     */
    @Operation(
        operationId = "findAllTasks",
        summary = "Get all tasks.",
        description = "Returns all tasks.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved tasks.")
        ]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAll(): ResponseEntity<List<Task>> = ResponseEntity.ok(taskService.findAll())

    /**
     * Retrieves a page of tasks.
     *
     * This endpoint returns a paginated list of tasks based on the provided page number and page size.
     * It is useful for clients that need to display tasks in a paginated format.
     *
     * @param pageNumber the number of the page to retrieve, starting from 0.
     * @param pageSize the size of the page to retrieve. If not provided, defaults to 20.
     * @return [ResponseEntity] containing a Page of Task objects.
     */
    @Operation(
        operationId = "findPageWithTasks",
        summary = "Get tasks by page.",
        description = "Returns a page of tasks based on page number and page size.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved page of tasks.")
        ]
    )
    @GetMapping(value = ["/page"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findPage(@RequestParam pageNumber: Int, @RequestParam(required = false) pageSize: Int?): ResponseEntity<Page<Task>> = ResponseEntity.ok(taskService.findPage(pageNumber, pageSize ?: 20))

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id The [UUID] of the task to retrieve.
     * @return A [ResponseEntity] containing the Task if found, or a 404 status if not found.
     */
    @Operation(
        operationId = "findTaskById",
        summary = "Get task by id.",
        description = "Returns an task with the specified id."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved task."),
            ApiResponse(responseCode = "404", description = "Task not found.")
        ]
    )
    @GetMapping(value = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(@PathVariable id: UUID): ResponseEntity<Task> = ResponseEntity.ofNullable(taskService.findById(id))

    /**
     * Creates a new task with the provided model and optional files.
     *
     * @param model The [TaskAddRequest] object containing the task data.
     * @param files An optional collection of [MultipartFile] objects representing associated files.
     * @return A [ResponseEntity] indicating the result of the creation operation.
     */
    @Operation(
        operationId = "createTask",
        summary = "Create task.",
        description = "Creates a new task based on the provided model.",
        requestBody =
            io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = [Content(encoding = [Encoding(name = "model", contentType = "application/json")]
                )]
            )
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Task created successfully."),
            ApiResponse(responseCode = "400", description = "Invalid request data.")
        ]
    )
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun create(
        @Valid @RequestPart(name = "model") model: TaskAddRequest,
        @RequestPart(value = "files", required = false) files: Collection<MultipartFile>?
    ): ResponseEntity<Void> {
        val task = modelMapper.map(model, Task::class.java)
        return insert(task, files)
    }

    /**
     * Creates a new draft task based on the provided model.
     *
     * @param model The [TaskDraftRequest] object containing the draft task data.
     * @return A [ResponseEntity] indicating the result of the creation operation.
     */
    @Operation(
        operationId = "createDraftTask",
        summary = "Create draft task.",
        description = "Creates a new task based on the provided model."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Task created successfully."),
            ApiResponse(responseCode = "400", description = "Invalid request data.")
        ]
    )
    @PostMapping(value = ["/draft"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@Valid @RequestBody model: TaskDraftRequest): ResponseEntity<Void> =
        insert(modelMapper.map(model, Task::class.java))

    /**
     * Updates the OCR configuration for a specific task.
     *
     * @param id The [UUID] of the task to update.
     * @param ocrConfigRequest The [OcrConfigRequest] object containing the new OCR configuration data.
     * @return A [ResponseEntity] with no content, indicating the update was successful.
     */
    @Operation(
        operationId = "updateTaskConfig",
        summary = "Updates ocr configuration for task with given id.",
        description = "Updates ocr configuration for task with given id."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Task's ocr configuration successfully updated."),
            ApiResponse(responseCode = "400", description = "Invalid request data.")
        ]
    )
    @PutMapping("/config/{id}")
    fun update(@PathVariable id: UUID, @RequestBody ocrConfigRequest: OcrConfigRequest): ResponseEntity<Void> {
        modelMapper.map(ocrConfigRequest, OcrConfig::class.java).let {
            taskService.update(id, it)
            return ResponseEntity.noContent().build()
        }
    }

    /**
     * Updates the scheduler configuration for a specific task.
     *
     * @param id The [UUID] of the task to update.
     * @param schedulerConfigRequest The [SchedulerConfigRequest] object containing the new scheduler configuration data.
     * @return A [ResponseEntity] with no content, indicating the update was successful.
     */
    @Operation(
        operationId = "updateTaskScheduler",
        summary = "Updates scheduler configuration for task with given id.",
        description = "Updates scheduler configuration for task with given id."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Task's scheduler configuration successfully updated."),
            ApiResponse(responseCode = "400", description = "Invalid request data.")
        ]
    )
    @PutMapping("/scheduler/{id}")
    fun update(@PathVariable id: UUID, @RequestBody schedulerConfigRequest: SchedulerConfigRequest): ResponseEntity<Void> {
        val schedulerConfig = modelMapper.map(schedulerConfigRequest, SchedulerConfig::class.java)
        taskService.update(id, schedulerConfig)
        return ResponseEntity.noContent().build()
    }

    /**
     * Updates the language setting for a specific task.
     *
     * @param id The [UUID] of the task to update.
     * @param language The new language setting to be applied to the task.
     * @return A [ResponseEntity] with no content, indicating the update was successful.
     */
    @Operation(
        operationId = "updateTaskLanguage",
        summary = "Updates language of ocr configuration for task with given id.",
        description = "Updates language of ocr configuration for task with given id."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Task's language of ocr configuration successfully updated."),
            ApiResponse(responseCode = "400", description = "Invalid request data.")
        ]
    )
    @PatchMapping("/language/{id}")
    fun updateLanguage(@PathVariable id: UUID, @RequestParam(required = true) language: String): ResponseEntity<Void> {
        taskService.update(id, language)
        return ResponseEntity.noContent().build()
    }

    /**
     * Uploads files associated with a specific task.
     *
     * @param id The [UUID] of the task to which the files are associated.
     * @param multipartFiles A collection of [MultipartFile] objects representing the files to be uploaded.
     * @return A [ResponseEntity] containing a list of [UploadDocumentResponse] objects, each representing an uploaded file.
     */
    @Operation(
        operationId = "uploadFiles",
        summary = "Upload files and create task's in documents for given id.",
        description = "Upload files and create task's in documents for given id."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Task's in documents configuration successfully updated created and files successfully uploaded."),
            ApiResponse(responseCode = "400", description = "Invalid request data.")
        ]
    )
    @PutMapping("upload/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun uploadFile(
        @PathVariable id: UUID,
        @RequestPart("files") multipartFiles: Collection<MultipartFile>
    ): ResponseEntity<List<UploadDocumentResponse>> =
        ResponseEntity.ok(taskService.upload(id, multipartFiles).map { UploadDocumentResponse(it.randomizedFileName, it.originalFileName) })

    /**
     * Removes a file associated with a task.
     *
     * This function removes a specific file from a task if the `originalFileName` is provided.
     * If no `originalFileName` is specified, it removes all files associated with the task.
     *
     * @param id The unique identifier of the task.
     * @param originalFileName The original name of the file to be removed. If not provided, all files will be removed.
     * @return A [ResponseEntity] with no content, indicating the operation was successful.
     */
    @Operation(
        operationId = "removeFile",
        summary = "Remove file or all files and document from task.",
        description = "Remove file or all files (in case that param originalFileName is not give) and document from task."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "File removed from file system successfully. Also document task updated successfully."),
            ApiResponse(responseCode = "400", description = "Invalid request data.")
        ]
    )
    @DeleteMapping("/file/{id}")
    fun removeFile(@PathVariable id: UUID, @RequestParam(required = false) originalFileName: String): ResponseEntity<Void> {
        if (originalFileName.isEmpty()) {
            taskService.removeAllFiles(id)
        } else {
            taskService.removeFile(id, originalFileName)
        }
        return ResponseEntity.noContent().build()
    }

    /**
     * Deletes a task by its unique identifier.
     *
     * This function delegates the deletion of a task to the `TaskService`.
     * It removes the task associated with the provided ID from the system.
     *
     * @param id The unique identifier of the task to be deleted.
     * @return A [ResponseEntity] with no content, indicating the operation was successful.
     */
    @Operation(
        operationId = "deleteTask",
        summary = "Delete task and remove all files.",
        description = "Delete task and remove all files."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Task deleted and all files removed from file system successfully."),
            ApiResponse(responseCode = "400", description = "Invalid request data.")
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteById (@PathVariable id: UUID): ResponseEntity<Void> {
        taskService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * Inserts a new task and optionally uploads associated files.
     *
     * @param task The [Task] entity to be inserted.
     * @param files An optional collection of [MultipartFile] objects representing associated files.
     * @return A [ResponseEntity] with the location of the created task.
     */
    private fun insert(task: Task, files: Collection<MultipartFile>? = null): ResponseEntity<Void> {
        val location = taskService.insert(task, files).let {
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(it.id)
                .toUri()
        }

        return ResponseEntity.created(location).build()
    }
}