package io.github.bodzisz.aop;

import io.github.bodzisz.service.PostService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Aspect
@Component
public class PaginationValidator {
    private final Logger logger = LoggerFactory.getLogger(PaginationValidator.class);
    private PostService postService;

    public PaginationValidator(PostService postService) {
        this.postService = postService;
    }

    @Around(value = "execution(String io.github.bodzisz.controller.PostController.showPagedPosts(..))")
    public Object beforeShowPostsPaged(ProceedingJoinPoint  proceedingJoinPoint) throws Throwable {
        Object[] validArgs = proceedingJoinPoint.getArgs();
        int index = 0;
        int totalPages = postService.findAllSortedPaged(0).getTotalPages();
        Model model = null;
        int targetPage;

        for(Object o : validArgs) {
            if(o instanceof Model) {
                model = (Model)o;
                validArgs[index] = model;
            }
            else if(o instanceof Integer) {
                targetPage = (int)o;
                if(targetPage < 0) {
                    targetPage = 0;
                    model.addAttribute("invalidPageMessage", "Page does not exist!");
                }
                else if(targetPage >= totalPages) {
                    targetPage = totalPages - 1;
                    model.addAttribute("invalidPageMessage", "Page does not exist!");
                }
                validArgs[index] = targetPage;
            }
            index++;
        }

        return proceedingJoinPoint.proceed(validArgs);
    }
}
