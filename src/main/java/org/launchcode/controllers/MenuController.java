package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@Controller
@RequestMapping(value="menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;


    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "List of Menus");
        return "menu/index";
    }

    @RequestMapping(value="add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add a new Menu");
        Menu menu = new Menu();
        model.addAttribute("menu", menu);
        return "menu/add";
    }

    @RequestMapping(value="add", method = RequestMethod.POST)
    public String submitAddMenuForm(Model model,
                                    @ModelAttribute @Valid Menu menu, Errors errors) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add a new Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    /**
     * @RequestMapping(value = "edit/{cheeseId}", method = RequestMethod.GET)
    public String displayEditForm(Model model, @PathVariable int cheeseId) {
     */

    @RequestMapping(value="view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId) {
        //Category cat = categoryDao.findOne(categoryId);
        //Remember the autowired DAOs at the top of the class
        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("title", "Menu Contents");
        model.addAttribute("menu", menu);
        return "menu/view";
    }

    @RequestMapping(value="add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {
        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("title", "Add item to menu: "+menu.getName());

        AddMenuItemForm addMenuItemForm = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("form",addMenuItemForm);

        return "menu/add-item";
    }

    @RequestMapping(value="add-item/{menuId}", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm addMenuItemForm,
                          @RequestParam int menuId, @RequestParam int cheeseId, Errors errors) {
        if (errors.hasErrors()) {
            return "menu/add-item";
        }

        Cheese cheese = cheeseDao.findOne(cheeseId);
        Menu theMenu = menuDao.findOne(menuId);
        theMenu.addItem(cheese);
        menuDao.save(theMenu);
        return "redirect:/menu/view/{menuId}";
    }
}
