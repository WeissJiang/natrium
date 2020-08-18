package nano.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.service.WikiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/wiki")
public class WikiController {

    @NonNull
    private final WikiService wikiService;

    @GetMapping(path = "/extracts", produces = "application/json")
    public ResponseEntity<String> wikiExtracts(@RequestParam("title") String title) {
        String extracts = this.wikiService.getWikiExtracts(title);
        return ResponseEntity.ok(extracts);
    }
}
