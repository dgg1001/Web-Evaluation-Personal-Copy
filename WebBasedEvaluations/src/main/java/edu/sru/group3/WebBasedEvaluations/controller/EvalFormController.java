package edu.sru.group3.WebBasedEvaluations.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.SerializationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sru.group3.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.group3.WebBasedEvaluations.domain.EvaluationLog;
import edu.sru.group3.WebBasedEvaluations.domain.Group;
import edu.sru.group3.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.group3.WebBasedEvaluations.evalform.GenerateEvalReport;
import edu.sru.group3.WebBasedEvaluations.evalform.GenerateEvalReportPoi;
import edu.sru.group3.WebBasedEvaluations.evalform.ParseEvaluation;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.group3.WebBasedEvaluations.repository.GroupRepository;

/**
 * Controller for functionality of the 'eval_templates.html' web page for 'ADMIN_EVAL' users.
 * Includes uploading, saving, deleting, and downloading excel files and Evaluation forms.
 * 
 * @author Logan Racer
 */
@Controller
public class EvalFormController {

	private EvaluationRepository evalFormRepo;
	private EvaluationLogRepository evalLogRepo;
	private GroupRepository groupRepo;

	private Evaluation eval;
	private XSSFWorkbook apacheWorkbook;

	private final String TEMP_FILES_PATH = "src\\main\\resources\\temp\\";
	private Logger log = LoggerFactory.getLogger(EvalFormController.class);

	// Constructor
	EvalFormController(EvaluationRepository evalFormRepo, EvaluationLogRepository evalLogRepo, GroupRepository groupRepo) {
		this.evalFormRepo = evalFormRepo;
		this.evalLogRepo = evalLogRepo;
		this.groupRepo = groupRepo;

		this.eval= null;
		this.apacheWorkbook = null;
	}
	
	

	/**
	 * Controller method for @{/admin_evaluations}. Loads the "Evaluation Templates" page
	 * for the 'EVAL_ADMIN' users.
	 * 
	 * @param model
	 * @return eval_templates.html
	 */
	@GetMapping("/admin_evaluations")
	public String adminEvaluations(Model model) {


		// Create the directories if they do not exist, delete any existing files
		try {
			Files.createDirectories(Paths.get(TEMP_FILES_PATH));
			FileUtils.cleanDirectory(new File(TEMP_FILES_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + TEMP_FILES_PATH + "' could not be created or cleaned.");
		}

		String hasEvals = "no";
		if (evalFormRepo.count() > 0) {
			hasEvals = "yes";

			List <EvalTemplates> evalTempList = (List<EvalTemplates>) evalFormRepo.findAll();
			List <Evaluation> evalList = new ArrayList<Evaluation>();

			for (int i = 0; i < evalTempList.size(); i++) {

				// Deserialize
				byte[] data = evalTempList.get(i).getEval();
				Evaluation eval;
				eval = (Evaluation) SerializationUtils.deserialize(data);
				eval.clearGroupsList();

				// Checking which groups the evaluation is assigned
				List<Group> groupList = (List<Group>) groupRepo.findAll();

				for (int j = 0; j < groupList.size(); j++) {
					byte[] groupData = groupList.get(j).getEvalTemplates().getEval();

					if(data == groupData) {
						eval.addGroup("Group #" + groupList.get(j).getId());
					}
				}
				evalList.add(eval);
			}

			model.addAttribute("evalList", evalList);
		}

		model.addAttribute("hasEvals", hasEvals);

		return "eval_templates";
	}



	/**
	 * Controller method for @{/upload_eval}. Process the uploaded file.
	 * Redirects to the "Evaluation Templates" page for the 'EVAL_ADMIN' users
	 * updated with the evaluation preview or error messages.
	 * 
	 * @param file - Excel file uploaded
	 * @param redir - Redirect attributes
	 * @return RedirectView to @{/admin_evaluations}
	 * @throws Exception
	 */
	@RequestMapping(value = "/upload_eval", method = RequestMethod.POST)
	public Object uploadEvalTemplate(@RequestParam("file") MultipartFile file, RedirectAttributes redir) throws Exception {

		boolean showLog = false;

		if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
			redir.addFlashAttribute("error", "Wrong file type or no file selected");
		} else {

			// Temp XML file name
			final String XML_FILE_NAME = "eval.xml";

			// Load stream into new workbook
			InputStream stream = file.getInputStream();
			Workbook wb = new Workbook(stream);

			// Remove all but worksheet 0
			while(wb.getWorksheets().getCount() > 1) {
				wb.getWorksheets().removeAt(1);
			}

			// Save .xlsx as .xml file
			wb.save(TEMP_FILES_PATH + XML_FILE_NAME, SaveFormat.AUTO);

			// Save excel file as apache workbook
			XSSFWorkbook workbook = new XSSFWorkbook(new BufferedInputStream(file.getInputStream()));	
			this.apacheWorkbook = workbook;

			// Parse data from XML file into Evaluation object
			this.eval = new Evaluation();
			this.eval = ParseEvaluation.parseEvaluation(this.eval, TEMP_FILES_PATH + XML_FILE_NAME);

			// Check file ID and check for duplicates
			String id = this.eval.getEvalID();
			boolean duplicate = false;

			if (evalFormRepo.count() > 0) {

				List <EvalTemplates> evalTempList = (List<EvalTemplates>) evalFormRepo.findAll();
				for (int i = 0; i < evalTempList.size(); i++) {

					// Deserialize
					byte[] data = evalTempList.get(i).getEval();
					Evaluation eval;
					eval = (Evaluation) SerializationUtils.deserialize(data);
					if (eval.getEvalID().equals(id)) {
						duplicate = true;
					}
				}
			}

			// Check for duplicates
			if (duplicate) {
				this.eval.addError("Evaluation template with keyword '<code>ID</code>': &nbsp'<u>" + id + "</u>'&nbsp already exists.");
			}

			//Process tool tips
			this.eval.processToolTips();

			//Check for errors
			this.eval.checkErrors();

			if (this.eval.getWarningCount() > 0) {
				redir.addFlashAttribute("warningList", this.eval.getWarnings());

				log.warn("Evaluation file uploaded contains the following warnings:");
				for (int i = 0; i < this.eval.getWarningCount(); i++) {
					String warn = this.eval.getWarning(i);
					warn = warn.replaceAll("<code>", "");
					warn = warn.replaceAll("</code>", "");
					warn = warn.replaceAll("<u>", "");
					warn = warn.replaceAll("</u>", "");
					warn = warn.replaceAll("&nbsp", "");
					log.warn("\t" + warn);
				}
				showLog = true;
			}
			if (this.eval.getErrorCount() > 0) {
				redir.addFlashAttribute("errorList", this.eval.getErrors());

				log.error("Evaluation file uploaded failed with the following errors:");
				for (int i = 0; i < this.eval.getErrorCount(); i++) {
					String error = this.eval.getError(i);
					error = error.replaceAll("<code>", "");
					error = error.replaceAll("</code>", "");
					error = error.replaceAll("<u>", "");
					error = error.replaceAll("</u>", "");
					error = error.replaceAll("&nbsp", "");
					log.error("\t" + error);
				}
				showLog = true;
			} else {

				redir.addFlashAttribute("eval", this.eval);
				redir.addFlashAttribute("completed", "Preview the template below and click 'Save Evaluation Template' if satisfied.");

				log.info("Evaluation template '" + this.eval.getEvalID() + "' uploaded successfully.");
			}
		}
		if (showLog) {
			if (this.eval != null) {
				if (!this.eval.getEvalID().isBlank()) {
					redir.addFlashAttribute("showLog", this.eval.getEvalID());
				} else {
					redir.addFlashAttribute("showLog", "NoID");
				}
			}

		}

		RedirectView redirectView = new RedirectView("/admin_evaluations", true);

		return redirectView;
	}



	/**
	 * Saves the uploaded evaluation in the database. Redirects back to @{/admin_evaluations} with the normal view.
	 * 
	 * @param eval - Evaluation object
	 * @param model
	 * @return RedirectView to @{/admin_evaluations}
	 * @throws Exception
	 */
	@RequestMapping("/eval_form")
	public RedirectView saveEvalTemplate(@Validated Evaluation eval, Model model) throws Exception {

		// Create the directories if they do not exist, delete any existing files
		try {
			Files.createDirectories(Paths.get(TEMP_FILES_PATH));
			FileUtils.cleanDirectory(new File(TEMP_FILES_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + TEMP_FILES_PATH + "' could not be created or cleaned.");
		}

		// Temp file name
		final String FILE_NAME = "eval_temp.xlsx";

		// Serialize eval
		byte[] evalByte;
		evalByte = SerializationUtils.serialize(this.eval);

		try (OutputStream out = new FileOutputStream(TEMP_FILES_PATH + FILE_NAME)) {
			this.apacheWorkbook.write(out);
		} catch (IOException e){
			e.printStackTrace();
			log.error("File '" + TEMP_FILES_PATH + FILE_NAME + "' could not be created.");
		}

		// Serialize apache workbook excel file
		byte[] excelByte = Files.readAllBytes(Paths.get(TEMP_FILES_PATH + FILE_NAME));

		try {
			FileUtils.cleanDirectory(new File(TEMP_FILES_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + TEMP_FILES_PATH + "' could not be cleaned.");
		}

		// Save to database
		EvalTemplates evalTemp = new EvalTemplates(this.eval.getEvalID(), evalByte, excelByte);
		evalFormRepo.save(evalTemp);
		log.info("Evaluation template '" + this.eval.getEvalID() + "' saved.");

		// Redirect
		RedirectView redirectView = new RedirectView("/admin_evaluations", true);
		return redirectView;
	}
	
	
	
	/**
	 * Processes the request for the download of the Evaluation Results excel file for a given evaluation ID.
	 * 
	 * @param evalId - ID of the Evaluation
	 * @return ResponseEntity containing the download resource
	 * @throws Exception
	 */
	@GetMapping("/download_eval_results/{evalId}")
	public ResponseEntity<Resource> downloadEvalResults(@PathVariable("evalId") String evalId) throws Exception {

		// Name of download file
		final String FILE_NAME = "Evaluation Summary - " + evalId + ".xlsx";

		log.info("File '" + FILE_NAME + "' requested for download.");

		// Create the directories if they do not exist, delete any existing files
		try {
			Files.createDirectories(Paths.get(TEMP_FILES_PATH));
			FileUtils.cleanDirectory(new File(TEMP_FILES_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + TEMP_FILES_PATH + "' could not be created or cleaned.");
		}

		//Get Evaluation template
		List<EvalTemplates> evalTemps = (List<EvalTemplates>) evalFormRepo.findAll();
		byte[] evalTempByte = null;

		for (int i = 0; i<evalTemps.size();i++) {
			String name = evalTemps.get(i).getName();
			//System.out.println("name: " + name + ", evalId: " + evalId);

			if (name.equals(evalId)) {
				//System.out.println("Eval template found");
				evalTempByte = evalTemps.get(i).getEval();
			}
		}

		Evaluation evalTemp = (Evaluation) SerializationUtils.deserialize(evalTempByte);

		//Get Completed evaluations
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evalLogRepo.findAll();
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();

		byte[] evalLogByte = null;

		for (int i = 0; i < evalLogs.size();i++) {
			if (evalLogs.get(i).getCompleted()) {
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);

				if(completeEval.getEvalID().equals(evalId)) {
					completedEvals.add(completeEval);
				}
			}
		}

		log.info("Found " + completedEvals.size() + " completed evals for Evaluation form ID: '" + evalId + "'.");

		// Create the excel report file
		
		// Uncomment to generate using Aspose.Cells
		//GenerateEvalReport.generateReport(evalTemp, completedEvals, TEMP_FILES_PATH, FILE_NAME);
		
		// Generate excel using Apache POI
		GenerateEvalReportPoi.generateReport(evalTemp, completedEvals, TEMP_FILES_PATH, FILE_NAME);

		//Download the file
		FileSystemResource resource = new FileSystemResource(TEMP_FILES_PATH + FILE_NAME);
		MediaType mediaType = MediaTypeFactory
				.getMediaType(resource)
				.orElse(MediaType.APPLICATION_OCTET_STREAM);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		ContentDisposition disposition = ContentDisposition
				.attachment()
				.filename(resource.getFilename())
				.build();
		headers.setContentDisposition(disposition);

		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
	
	
	
	/**
	 * Processes the request for the download of the original Evaluation template excel file for a given evaluation ID.
	 * 
	 * @param evalId - ID of the Evaluation
	 * @return ResponseEntity containing the download resource
	 * @throws Exception
	 */
	@GetMapping("/download_eval_excel/{evalId}")
	public ResponseEntity<Resource> downloadEvalExcel(@PathVariable("evalId") String evalId) throws Exception {

		// Name of download file
		final String FILE_NAME = "Evaluation File - " + evalId + ".xlsx";

		log.info("File '" + FILE_NAME + "' requested for download.");

		// Create the directories if they do not exist, delete any existing files
		try {
			Files.createDirectories(Paths.get(TEMP_FILES_PATH));
			FileUtils.cleanDirectory(new File(TEMP_FILES_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + TEMP_FILES_PATH + "' could not be created or cleaned.");
		}

		//Get Evaluation template
		List<EvalTemplates> evalTemps = (List<EvalTemplates>) evalFormRepo.findAll();
		byte[] excelByte = null;

		for (int i = 0; i<evalTemps.size();i++) {
			String name = evalTemps.get(i).getName();

			if (name.equals(evalId)) {
				excelByte = evalTemps.get(i).getExcelFile();
			}
		}

		Path path = Paths.get(TEMP_FILES_PATH + FILE_NAME);
		Files.write(path, excelByte);

		//Download the file
		FileSystemResource resource = new FileSystemResource(TEMP_FILES_PATH + FILE_NAME);
		MediaType mediaType = MediaTypeFactory
				.getMediaType(resource)
				.orElse(MediaType.APPLICATION_OCTET_STREAM);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		ContentDisposition disposition = ContentDisposition
				.attachment()
				.filename(resource.getFilename())
				.build();
		headers.setContentDisposition(disposition);

		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
	
	
	
	/**
	 * Processes the request for the download of the uploaded Evaluation template
	 * excel file with Errors and Warnings appended to a new sheet.
	 * 
	 * @param evalId - ID of the Evaluation
	 * @return ResponseEntity containing the download resource
	 * @throws Exception
	 */
	@GetMapping("/dl_error_log/{evalId}")
	public ResponseEntity<Resource> downloadErrorLog(@PathVariable("evalId") String evalId) throws Exception {

		// Name of download file
		final String FILE_NAME = "Uploaded Evaluation - " + evalId + ".xlsx";

		log.info("File '" + FILE_NAME + "' requested for download.");

		// Create the directories if they do not exist, delete any existing files
		try {
			Files.createDirectories(Paths.get(TEMP_FILES_PATH));
			FileUtils.cleanDirectory(new File(TEMP_FILES_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + TEMP_FILES_PATH + "' could not be created or cleaned.");
		}

		XSSFWorkbook errorLogFile;
		errorLogFile = this.apacheWorkbook;
		errorLogFile.createSheet("Error & Warning Log");
		XSSFSheet warnSheet = errorLogFile.getSheet("Error & Warning Log");
		int row = 0;
		for (int i = 0; i < this.eval.getErrorCount(); i++) {
			String error = this.eval.getError(i);
			error = "ERROR: " + error;
			error = error.replaceAll("<code>", "");
			error = error.replaceAll("</code>", "");
			error = error.replaceAll("<u>", "");
			error = error.replaceAll("</u>", "");
			error = error.replaceAll("&nbsp", "");

			XSSFRow curRow = warnSheet.createRow(i);
			curRow.createCell(0).setCellValue(error);
			row++;
		}

		for (int i = 0; i < this.eval.getWarningCount(); i++) {
			String warn = this.eval.getError(i);
			warn = "WARNING: " + warn;
			warn = warn.replaceAll("<code>", "");
			warn = warn.replaceAll("</code>", "");
			warn = warn.replaceAll("<u>", "");
			warn = warn.replaceAll("</u>", "");
			warn = warn.replaceAll("&nbsp", "");

			XSSFRow curRow = warnSheet.createRow(row);
			curRow.createCell(0).setCellValue(warn);
			row++;
		}

		try (OutputStream out = new FileOutputStream(TEMP_FILES_PATH + FILE_NAME)) {
			errorLogFile.write(out);
		} catch (IOException e){
			e.printStackTrace();
			log.error("File '" + TEMP_FILES_PATH + FILE_NAME + "' could not be created.");
		}

		//Download the file
		FileSystemResource resource = new FileSystemResource(TEMP_FILES_PATH + FILE_NAME);
		MediaType mediaType = MediaTypeFactory
				.getMediaType(resource)
				.orElse(MediaType.APPLICATION_OCTET_STREAM);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		ContentDisposition disposition = ContentDisposition
				.attachment()
				.filename(resource.getFilename())
				.build();
		headers.setContentDisposition(disposition);

		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}



	/**
	 * Processes the request to delete an evaluation template from a given evaluation ID.
	 * 
	 * @param evalId - ID of the Evaluation
	 * @param redir - Redirect attributes
	 * @return RedirectView to @{/admin_evaluations}
	 */
	@RequestMapping("/del_eval_form/{evalId}")
	public RedirectView deleteEvalTemplate(@PathVariable("evalId") String evalId, RedirectAttributes redir) {

		evalFormRepo.deleteById(evalId);
		log.info("Evaluation template '" + evalId + "' deleted.");

		RedirectView redirectView = new RedirectView("/admin_evaluations", true);
		return redirectView;
	}
}