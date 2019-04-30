package hello;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
public class WebController implements WebMvcConfigurer {
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/result").setViewName("result");
	}

	@GetMapping("/") // this method include a persionform parameter in the signature, so the form template can associate form attributes with a PersonForm
	public String showForm(PersonForm personForm) {
		return "form";
	}

	/**
	* This method accept two arguments
	* the personForm object marked up with @Valid to gather attributes filled out in the form you're about to build
	* the bindignResult object so you can test for and retrieve validation errors
	*/
	@PostMapping("/")
	public String checkPersonInfo(@Valid PersonForm personForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "form";
		}
		return "redirect:/result";
	}
}
