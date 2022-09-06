package edu.sru.group3.WebBasedEvaluations.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.SerializationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import edu.sru.group3.WebBasedEvaluations.domain.EvaluationLog;
import edu.sru.group3.WebBasedEvaluations.domain.Evaluator;
import edu.sru.group3.WebBasedEvaluations.domain.EvaluatorId;
import edu.sru.group3.WebBasedEvaluations.domain.Group;
import edu.sru.group3.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.group3.WebBasedEvaluations.domain.Reviewee;
import edu.sru.group3.WebBasedEvaluations.domain.SelfEvaluation;
import edu.sru.group3.WebBasedEvaluations.domain.Archive;
import edu.sru.group3.WebBasedEvaluations.domain.EvalRole;
import edu.sru.group3.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.group3.WebBasedEvaluations.domain.User;
import edu.sru.group3.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.group3.WebBasedEvaluations.evalform.GenerateEvalReport;
import edu.sru.group3.WebBasedEvaluations.evalform.GenerateEvalReportPoi;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.group3.WebBasedEvaluations.repository.RevieweeRepository;
import edu.sru.group3.WebBasedEvaluations.repository.ArchiveRepository;
import edu.sru.group3.WebBasedEvaluations.repository.EvalRoleRepository;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;
import edu.sru.group3.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.group3.WebBasedEvaluations.excel.ExcelRead_group;

/**
 * Group controller determines the group behavior in the application
 *
 */
@Controller
public class GroupController {
	
	private Logger log = LoggerFactory.getLogger(GroupController.class);
	private GroupRepository groupRepository;

	private UserRepository userRepository;

	private EvaluatorRepository evaluatorRepository;
	private EvaluationLogRepository evaluationLogRepository;
	private RevieweeRepository revieweeRepository;
	private EvalRoleRepository roleRepository;
	private EvaluationRepository evaluationRepository;
	private EvaluationRepository evalFormRepo;
	private ArchiveRepository archiveRepository ;
	
	private final String TEMP_FILES_PATH = "src\\main\\resources\\temp\\";
	
	public GroupController(GroupRepository groupRepository, UserRepository userRepository,
			EvaluatorRepository evaluatorRepository, RevieweeRepository revieweeRepository,
			EvaluationLogRepository evaluationLogRepository, EvalRoleRepository roleRepository,
			EvaluationRepository evaluationRepository, EvaluationRepository evalFormRepo,
			ArchiveRepository archiveRepository
			) {
		this.evaluatorRepository = evaluatorRepository;
		this.groupRepository = groupRepository;
		this.userRepository = userRepository;
		this.revieweeRepository = revieweeRepository;
		this.evaluationLogRepository = evaluationLogRepository;
		this.roleRepository = roleRepository;
		this.evaluationRepository = evaluationRepository;
		this.evalFormRepo = evalFormRepo;
		this.archiveRepository=archiveRepository;
	}

	@RequestMapping(value = "/addgroup", method = RequestMethod.POST)
	public String addSave(@ModelAttribute("group") Group group,
			@RequestParam(value = "rev", required = false) long[] rev,
			@RequestParam(value = "lone", required = false) long lone,
			@RequestParam(value = "ltwo", required = false) long ltwo,
			@RequestParam(value = "facetoface", required = false) long facetoface, BindingResult bindingResult,
			Model model) {
		if (rev != null) {

			Reviewee reviewee = null;
			User user = null;
			for (int i = 0; i < rev.length; i++) {
				user = userRepository.findByid(rev[i]);
				reviewee = new Reviewee(group, user.getName(), user);
				group.appendReviewee(reviewee);
			}
			for (int i = 0; i < group.getReviewee().size(); i++) {
				
			}
			long id = group.getId();
			groupRepository.save(group);

			Evaluator eval1 = new Evaluator(userRepository.findByid(lone), group,
					roleRepository.findById(1).orElse(null));
			Evaluator eval2 = new Evaluator(userRepository.findByid(ltwo), group,
					roleRepository.findById(1).orElse(null));
			Evaluator eval3 = new Evaluator(userRepository.findByid(facetoface), group,
					roleRepository.findById(1).orElse(null));

			evaluatorRepository.save(eval1);
			evaluatorRepository.save(eval2);
			evaluatorRepository.save(eval3);
			List<Reviewee> revieweelist = revieweeRepository.findBygroup_Id(id);
			for (int a = 0; a < revieweelist.size(); a++) {

				evaluationLogRepository.save(new EvaluationLog(eval1, revieweelist.get(a)));
				evaluationLogRepository.save(new EvaluationLog(eval2, revieweelist.get(a)));
				evaluationLogRepository.save(new EvaluationLog(eval3, revieweelist.get(a)));
			}

		}

		return "home";

	}

	
	
	
	
	/**editgroup
	 * takes user to the eidt goup page where use can make changes to evaluator, reviewee and group attributes.
	 * @param redir
	 * @param id  of the group being edited 
	 * @param model model is the a model object use to add attributes to a web page 
	 * @return user to the group edit page
	 */
	@GetMapping("/editgroup/{id}")
	public Object editgroup(RedirectAttributes redir ,@PathVariable("id") long id, Model model) {
		
		Group group = groupRepository.findById(id);
		if(group.getEvalstart()) {
			RedirectView redirectView = new RedirectView("/admin_groups", true);
			redir.addFlashAttribute("error", "Can not  edit group "+id +", evaluation have started");
			return redirectView;
		}
		model.addAttribute("group", group);
		List<Boolean> synclist = new ArrayList<Boolean>();
		List<Boolean> prevlist = new ArrayList<Boolean>();
		List<Evaluator> evallist = evaluatorRepository.findByGroupId(id);
		
		List<EvalRole> roles = (List<EvalRole>) roleRepository.findAll();
		List<Reviewee> rev = revieweeRepository.findBygroup_Id(id);
		List<Long> revid = new ArrayList<Long>();
		for (int x = 0; x < rev.size(); x++) {
			revid.add(rev.get(x).getUser().getId());
		}
		Map<EvalRole, List<Long>> evals = new LinkedHashMap<EvalRole, List<Long>>();

		for (int x = 0; x < roles.size(); x++) {
			
			evals.put(roles.get(x), null);
			List<Long> temp = new ArrayList<Long>();
			for (int y = 0; y < evallist.size(); y++) {

				if (roles.get(x).getId() == evallist.get(y).getLevel().getId()) {
					temp.add((long) evallist.get(y).getUser().getId());
					
				}

			}
			evals.put(roles.get(x), temp);
		}
		for (int x = 0; x < roles.size(); x++) {
			int size = synclist.size();
			for (int y = 0; y < evallist.size(); y++) {

				if (roles.get(x).getId() == evallist.get(y).getLevel().getId()) {

					synclist.add(evallist.get(y).isSync());
			
					break;
				}
			}
			if(size == synclist.size()) {
				synclist.add(false);
			}

		}
		for (int x = 0; x < roles.size(); x++) {
			int size = prevlist.size();
			for (int y = 0; y < evallist.size(); y++) {

				if (roles.get(x).getId() == evallist.get(y).getLevel().getId()) {

					prevlist.add(evallist.get(y).isPreview());
					
					break;
				}
			}
			if(size == prevlist.size()) {
				prevlist.add(false);
			}


		}
		synclist.add(false);
		EvalRole temprole = roles.get(0);
		//System.out.println(evals.get(temprole));
		//System.out.println(evallist.get(0).getId());
		model.addAttribute("evallist", evals);
		model.addAttribute("sync", synclist);
		model.addAttribute("prev", prevlist);
		model.addAttribute("revedit", revid);
		model.addAttribute("users", userRepository.findByRolesOrRoles("USER","EVALUATOR_EVAL",Sort.by(Sort.Direction.ASC, "lastName")));
		model.addAttribute("usersEval", userRepository.findByRolesOrRoles("EVALUATOR","EVALUATOR_EVAL",Sort.by(Sort.Direction.ASC, "lastName")));
		model.addAttribute("rolles", roles);
		model.addAttribute("forms", evalFormRepo.findAll());
		log.info("group_edit open");
		return "group_edit";

	}


	/**update group 
	 * save changes to the group 
	 * @param id of the group being edited 
	 * @param rev return a new list of reviewee
	 * @param eval list of evaluators 
	 * @param roles list of roles
	 * @param issync determines if a evaluator is issync or not 
	 * @param isprev is the evaluation able to be previewed 
	 * @param form what form is associated  with the group
	 * @param self  is self evaluation needed 
	 * @param model
	 * @return  user back to the edit group page 
	 */
	@RequestMapping(value = "/updategroup{id}", method = RequestMethod.POST)
	public String update(@PathVariable("id") long id, @RequestParam(value = "rev", required = false) long[] rev,
			@RequestParam(value = "eval", required = false) long[] eval,
			@RequestParam(value = "role", required = false) int[] roles,
			@RequestParam(value = "issync", required = false) boolean[] issync,
			@RequestParam(value = "isprev", required = false) boolean[] isprev,
			@RequestParam(value = "form", required = false) EvalTemplates form,
			@RequestParam(value = "selfeval", required = false) boolean self, Model model) {
		
		//System.out.println("start");
		//for (int x = 0; x < isprev.length; x++) {
		//	System.out.println(isprev[x]);
		//}
		//System.out.println("end");
		//System.out.println(isprev.length);
		//System.out.println(roles.length);

		//  edits group 
		Group group = groupRepository.findById(id);

		group.getReviewee().clear();
		group.getEvaluator().clear();
		Reviewee reviewee = null;
		User user = null;
		for (int i = 0; i < rev.length; i++) {
			user = userRepository.findByid(rev[i]);
			reviewee = new Reviewee(group, user.getName(), user);
			group.appendReviewee(reviewee);
		}
		//for (int i = 0; i < group.getReviewee().size(); i++) {
		//	System.out.println(group.getReviewee().get(i).getName());
		//}
		group.setSelfeval(self);
		group.setEvalTemplates(form);
		groupRepository.save(group);
		group = groupRepository.findById(id);

		
		List<Reviewee> revieweelist = revieweeRepository.findBygroup_Id(id);
		

		
	

		for (int i = 0; i < roles.length; i++) {
			Evaluator temp = new Evaluator(userRepository.findByid(eval[i]), group,
					roleRepository.findById(roles[i]).orElse(null));
			temp.setSync(issync[roles[i] - 1]);
			temp.setPreview(isprev[roles[i] - 1]);
			List<Evaluator> eval2 = (evaluatorRepository.findByLevelIdAndGroupId(roles[i] - 1, group.getId()));
			List<Evaluator> eval3 = (evaluatorRepository.findByLevelIdAndGroupId(roles[i] + 1, group.getId()));
			for (int a = 0; a < revieweelist.size(); a++) {
				EvaluationLog eltemp = new EvaluationLog(temp, revieweelist.get(a));

				for (int k = 0; k < eval2.size(); k++) {
					EvaluationLog log1 = evaluationLogRepository.findByEvaluatorIdAndRevieweeId(eval2.get(k).getId(),
							revieweelist.get(a).getId());
					if ((eval2.get(k).isSync() != true) && log1.getAuth()) {
						eltemp.setAuth(true);

					} else {
						eltemp.setAuth(false);

					}
				}
				temp.appendEvalutationLog(eltemp);
				for (int k = 0; k < eval3.size(); k++) {
					EvaluationLog log2 = evaluationLogRepository.findByEvaluatorIdAndRevieweeId(eval3.get(k).getId(),
							revieweelist.get(a).getId());
					if ((temp.isSync() != true) && eltemp.getAuth()) {
						log2.setAuth(true);

						evaluationLogRepository.save(log2);

					}

				}

			

			}
			evaluatorRepository.save(temp);
		}

		
		return "redirect:/editgroup/" + id;

	}

	/**uploadgroup
	 * takes an excel file and takes the data from it and creates groups 
	 * @param reapExcelDataFile read the excel file 
	 * @param redir hold redirection attributes 
	 * @return admingroup page 
	 */
	@RequestMapping(value = "/uploadgroup", method = RequestMethod.POST)
	public Object uploadgroup(@RequestParam("file") MultipartFile reapExcelDataFile, RedirectAttributes redir) {
		
		XSSFSheet sheet = null;
		XSSFSheet sheet2 = null;
		XSSFSheet sheet3 = null;
		List<Group> grouplist = new ArrayList<Group>();
		List<Evaluator> evaluatorlist = new ArrayList<Evaluator>();
		List<EvalRole> rolelist = new ArrayList<EvalRole>();
		Map<Long, List<String>> syncmap = new HashMap<Long, List<String>>();
		Map<Long, List<String>>  previewmap = new HashMap<Long, List<String>>();
		try {
			sheet = ExcelRead_group.loadFile(reapExcelDataFile).getSheetAt(0);
			sheet2 = ExcelRead_group.loadFile(reapExcelDataFile).getSheetAt(1);
			sheet3 = ExcelRead_group.loadFile(reapExcelDataFile).getSheetAt(2);
		} catch (Exception e) {

			RedirectView redirectView = new RedirectView("/admin_groups", true);
			redir.addFlashAttribute("error", "invalid file");
			return redirectView;
		}
		String type = ExcelRead_group.checkStringType(sheet3.getRow(0).getCell(1));
		if (!type.equals("Groups")) {
			RedirectView redirectView = new RedirectView("/admin_groups", true);
			redir.addFlashAttribute("error", "wrong file type");
			//System.out.println(ExcelRead_group.checkStringType(sheet3.getRow(0).getCell(1)));
			return redirectView;
		}

		// rolls
		for (int i = 1; sheet3.getRow(i) != null; i++) {
			int roll = ExcelRead_group.checkIntType(sheet3.getRow(i).getCell(1));
			String roll_name = ExcelRead_group.checkStringType(sheet3.getRow(i).getCell(0));
			rolelist.add(new EvalRole(roll_name, roll));
			//System.out.println(roll + " " + roll_name);

		}
		int totalrole = rolelist.size();
		roleRepository.saveAll(rolelist);
		// groups
		for (int i = 0; sheet.getRow(0).getCell(i) != null; i++) {
			List<String> synclist = new ArrayList<String>();
			List<String> previewlist = new ArrayList<String>();
			Group group = new Group();

			// long id = (Long) null;

			for (int x = 0; sheet.getRow(x) != null; x++) {
				// System.out.print(sheet.getRow(x).getCell(i));
				if (x == 0) {
					String groupstringid = ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i))
							.replaceAll("\\s", "").replace("Group", "");
					group.setId(Long.parseLong(groupstringid));
				}

				else if (x == 1) {
					String evaltemplateid = ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i));
					EvalTemplates evaltemp = evaluationRepository.findById(evaltemplateid).orElse(null);
					if (evaltemp == null) {
						RedirectView redirectView = new RedirectView("/admin_groups", true);
						redir.addFlashAttribute("error", "template " + evaltemplateid + "does not exist");
						//System.out.println("user dosnt not exist1 " + evaltemplateid);
						log.error("user does not exist " + evaltemplateid);
						return redirectView;
					}



					group.setEvalTemplates(evaltemp);

				}
				// is self eval needed
				else if (x == 2) {
					String self = ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)).replaceAll("\\s", "");
					if (self.equals("NoSelf-Eval")) {
						group.setSelfeval(false);
					} else if (self.equals("Self-Eval")) {
						group.setSelfeval(true);
					} else {
						RedirectView redirectView = new RedirectView("/admin_groups", true);
						redir.addFlashAttribute("error", "in group  " + group.getId()+ " layout");
						log.error("error in group  " + group.getId()+ " layout");
						return redirectView;
					}

				} else if (x < (2 + totalrole)) {
					String sync = ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)).replaceAll("\\s", "");
					//System.out.print(sync);
					if(sync.equals("Sync")||sync.equals("Async")) {
						
						synclist.add(sync);
					}else
					{
						RedirectView redirectView = new RedirectView("/admin_groups", true);
						redir.addFlashAttribute("error", " in group " + group.getId()+ " Sync layout");
						log.error("error in group " + group.getId()+ " Sync layout");
						return redirectView;
					}
					
				}
				else if (x < (2 + totalrole+totalrole)) {
					String preview = ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)).replaceAll("\\s", "");
					//System.out.print(preview);
					if(preview.equals("preview")||preview.equals("nopreview")) {
					
						previewlist.add(preview);
					}else {
						RedirectView redirectView = new RedirectView("/admin_groups", true);
						redir.addFlashAttribute("error", "in group  " + group.getId()+ " preview layout");
						log.error("error", "error in group  " + group.getId()+ " preview layout");
						return redirectView;
					}
					
				}


				else {
					if (ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)) != null) {
						User user = userRepository
								.findByName(ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)));
						if (user != null) {
							Reviewee reviewee = new Reviewee(group, user.getName(), user);
							group.appendReviewee(reviewee);

						} else {
							redir.addFlashAttribute("error", "user "
									+ ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)) + " dosnt not exist");

							RedirectView redirectView = new RedirectView("/admin_groups", true);
							//System.out.println("user dosnt not exist " + x + " " + i
							//		+ ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)));
							log.error("user" + ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)) + "does not not exist");
							return redirectView;

						}
					}
				}

			}
			syncmap.put(group.getId(), synclist);
			previewmap.put(group.getId(), previewlist);
			grouplist.add(group);

		}
		groupRepository.saveAll(grouplist);
		//// Evaluator
		for (int i = 0; sheet2.getRow(i) != null; i += 2) {
			User user = userRepository.findByName(ExcelRead_group.checkStringType(sheet2.getRow(i).getCell(0)));

			if (user != null) {
				for (int x = 1; sheet2.getRow(i).getCell(x) != null; x++) {
					String groupids = ExcelRead_group.checkStringType(sheet2.getRow(i).getCell(x));
					String level = ExcelRead_group.checkStringType(sheet2.getRow(i + 1).getCell(x));
					List<String> levellist = Stream.of(level.split(",")).map(String::trim).collect(Collectors.toList());
					List<String> groupIDlist = Stream.of(groupids.split(",")).map(String::trim)
							.collect(Collectors.toList());
					int size = rolelist.size();

					for (int y = 0; y < groupIDlist.size(); y++) {
						long groupid = Long.parseLong(groupIDlist.get(y));
						for (int z = 0; z < levellist.size(); z++) {
							// if evaluator is sync or not 
							Evaluator eval = new Evaluator(user, groupRepository.findById(groupid),
									roleRepository.findByName(levellist.get(z)));
							int num = eval.getLevel().getId();
							if (num != size && (syncmap.get(groupid).get(num - 1).equals("Async"))) {
								eval.setSync(false);

							} else if (num != size && syncmap.get(groupid).get(num - 1).equals("Sync")) {
								eval.setSync(true);
							} else if (num == size) {
								eval.setSync(true);
							}
							
							if ((previewmap.get(groupid).get(num - 1).equals("preview"))) {
								eval.setPreview(true);

							} else if (previewmap.get(groupid).get(num - 1).equals("nopreview")) {
								eval.setPreview(false);
							}

							List<Reviewee> rev = revieweeRepository.findBygroup_Id(groupid);
							List<Evaluator> eval2 = (evaluatorRepository.findByLevelIdAndGroupId(num - 1, groupid));
							List<Evaluator> eval3 = (evaluatorRepository.findByLevelIdAndGroupId(num + 1, groupid));
							for (int a = 0; a < rev.size(); a++) {
								EvaluationLog etemp = new EvaluationLog(eval, rev.get(a));
							

								for (int k = 0; k < eval2.size(); k++) {
									EvaluationLog log1 = evaluationLogRepository
											.findByEvaluatorIdAndRevieweeId(eval2.get(k).getId(), rev.get(a).getId());
									if ((eval2.get(k).isSync() != true) && log1.getAuth()) {
										etemp.setAuth(true);

									} else {
										etemp.setAuth(false);

									}
								}
								eval.appendEvalutationLog(etemp);
								for (int k = 0; k < eval3.size(); k++) {
									EvaluationLog log2 = evaluationLogRepository
											.findByEvaluatorIdAndRevieweeId(eval3.get(k).getId(), rev.get(a).getId());
									if ((eval.isSync() != true) && etemp.getAuth()) {
										log2.setAuth(true);

										evaluationLogRepository.save(log2);

									}

								}

							}

							evaluatorlist.add(eval);
							evaluatorRepository.save(eval);

						}
					}

				}
			} else {
				String name = ExcelRead_group.checkStringType(sheet2.getRow(i).getCell(0));
				//System.out.println("user dosnt not exist ");
				groupRepository.deleteAll(grouplist);
				roleRepository.deleteAll(rolelist);
				redir.addFlashAttribute("error", "user " + name + " dosnt not exist");
				RedirectView redirectView = new RedirectView("/admin_groups", true);
				//System.out.println("user dosnt not exist2" + name);
				return redirectView;
			}

		}
		
		redir.addFlashAttribute("completed", true);
		RedirectView redirectView = new RedirectView("/admin_groups", true);
		log.info("group was added ");
		return redirectView;
	}

	@GetMapping("/uploading")
	public String uploadgroup(Model model) {

		return "redirect:/admin_groups";
	}

	@GetMapping("/Evaluationgroups")
	public String evalGroups(Model model, Authentication authentication) {

		MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();
		User user = userRepository.findByid(userD.getID());
		List<Group> grouplist = (List<Group>) groupRepository.findByevaluatorUserId(userD.getID(),Sort.by(Sort.Direction.ASC, "Id"));
		grouplist = new ArrayList<Group>(new LinkedHashSet<Group>(grouplist));

		model.addAttribute("groups", grouplist);
		
		List<EvalRole> roles = (List<EvalRole>) roleRepository.findAll();
	
		model.addAttribute("myRole", userD.getRoles());
		model.addAttribute("roles", roles);
		model.addAttribute("id", userD.getID());
		model.addAttribute("evalu", user);
		model.addAttribute("groups", grouplist);
		log.info("EvaluationView was opened ");
		return "EvaluationView";
	}

	
	/**
	 * takes  an load information for the admin group age 
	 * @param model
	 * @return admin group page 
	 */
	@GetMapping("/admin_groups")
	public String Groups(Model model) {
		
		List<Group> grouplist = (List<Group>) groupRepository.findAll();
		List<EvalRole> roles = (List<EvalRole>) roleRepository.findAll();
		List<EvaluationLog> evalLog = (List<EvaluationLog>) evaluationLogRepository.findAll();
		
		
		model.addAttribute("evaluation", evalLog);
		model.addAttribute("roles", roles);
	
		List<String>warnings =new ArrayList<String>();
		for(int x=0; x<roles.size();x++) {
			for(int y=0; y<grouplist.size();y++) {
				Boolean temp = evaluatorRepository.existsBylevelAndGroup(roles.get(x),grouplist.get(y));
				if(temp ==false) {
					warnings.add("Group:"+" "+ grouplist.get(y).getId()+" is missing "+ roles.get(x).getName()+" Evaluator");
				}
			}
		}
		
		for(int y=0; y<grouplist.size();y++) {
			if(grouplist.get(y).getReviewee().isEmpty()) {
				warnings.add("Group:"+" "+ grouplist.get(y).getId()+" has no reviewee");
			}
		}
		model.addAttribute("warnings",warnings);
		if(grouplist.isEmpty()) {
			grouplist=null;
		}
		model.addAttribute("groups", grouplist);
		log.info("admin group was open ");
		return "admin_groups";
	}

	
	
	
	
	/**
	 * this method will delete a group and store it competed evaluation in the archive 
	 * @param id of the group being deleted
	 * @param model
	 * @return
	 */
	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") long id, Model model) {
		
		Group group = groupRepository.findById(id);
		List<Archive> Archivelist = new ArrayList<Archive>();
		for(int x =0; x< group.getEvaluator().size();x++) {
			List<EvaluationLog> temp = group.getEvaluator().get(x).getEvalutationLog();
			for(int y =0; y< temp.size();y++) {
				if(temp.get(y).getCompleted()) {
					Archive temp2 = new Archive(temp.get(y));
					Archivelist.add(temp2);
				}
				
			}
		}
		for(int x =0; x< group.getReviewee().size();x++) {
			SelfEvaluation temp = group.getReviewee().get(x).getSelfEvaluation();
			if(temp !=null) {
				if(temp.getCompleted()) {
					Archive temp2 = new Archive(temp);
					Archivelist.add(temp2);
				}
			}
			
		}
		groupRepository.delete(group);
		archiveRepository.saveAll(Archivelist);
		log.info("group"+id +" was deleted ");
		return "redirect:/admin_groups";
	}
	
	
	
	/**
	 * Processes the request for the download of the Evaluation Results excel file for a given group.
	 * 
	 * @param groupId - ID of the Group
	 * @return ResponseEntity containing the download resource
	 * @throws Exception
	 */
	@GetMapping("/download_eval_group_results/{groupId}")
	public ResponseEntity<Resource> downloadEvalGroupResults(@PathVariable("groupId") long groupId) throws Exception {
		
		Group group = groupRepository.findById(groupId);
		String evalId = group.getEvalTemplates().getName();
		
		// Name of download file
		final String FILE_NAME = "Group " + groupId + " Evaluation Summary - " + evalId + ".xlsx";
		
		log.info("File '" + FILE_NAME + "' requested for download.");

		// Create the temp directory if it does not exist
		Files.createDirectories(Paths.get(TEMP_FILES_PATH));

		//Get Evaluation template
		List<EvalTemplates> evalTemps = (List<EvalTemplates>) evalFormRepo.findAll();
		byte[] evalTempByte = null;
		
		for (int i = 0; i<evalTemps.size();i++) {
			String name = evalTemps.get(i).getName();
			//System.out.println("name: " + name + ", evalId: " + evalId);
			
			if (name.equals(evalId)) {
				//System.out.println("Eval template " + evalId + " found");
				evalTempByte = evalTemps.get(i).getEval();
			}
		}
		
		Evaluation evalTemp = (Evaluation) SerializationUtils.deserialize(evalTempByte);
		
		//Get Completed evaluations
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();
		
		byte[] evalLogByte = null;
		
		for (int i = 0; i < evalLogs.size();i++) {
			if (evalLogs.get(i).getCompleted()) {
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);
				
				if(completeEval.getEvalID().equals(evalId)) {
					
					String evalGroupNum = "";
					
					for (int j = 0; j < completeEval.getSection(0).getQuestionCount(); j++) {
						if (completeEval.getSection(0).getQuestion(j).getQText().equals("GROUP NO.")) {
							evalGroupNum = completeEval.getSection(0).getQuestion(j).getQResponse();
						}
					}
					
					if (evalGroupNum.matches(".*[0-9].*")) {
						evalGroupNum = evalGroupNum.replaceAll("\\D+","");
						if (Long.parseLong(evalGroupNum) == groupId) {
							completedEvals.add(completeEval);
						}
					}	
				}
			}
		}
		
		//System.out.println("FOUND " + completedEvals.size() + " COMPLETED EVALS FOR GROUP: " + groupId);
		log.info("Found " + completedEvals.size() + " completed evals for group " + groupId);
				
		// Create the excel report file
		//GenerateEvalReport.generateReport(evalTemp, completedEvals, TEMP_FILES_PATH, FILE_NAME);
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
}
