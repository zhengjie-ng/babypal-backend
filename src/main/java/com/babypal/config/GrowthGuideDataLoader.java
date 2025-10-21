package com.babypal.config;

import java.util.Arrays;
import org.springframework.stereotype.Component;
import com.babypal.models.GrowthGuide;
import com.babypal.repositories.GrowthGuideRepository;
import jakarta.annotation.PostConstruct;

@Component
public class GrowthGuideDataLoader {

    private final GrowthGuideRepository growthGuideRepository;

    public GrowthGuideDataLoader(GrowthGuideRepository growthGuideRepository) {
        this.growthGuideRepository = growthGuideRepository;
    }

    @PostConstruct
    public void loadGrowthGuides() {
        if (growthGuideRepository.count() > 0) {
            System.out.println("Growth guides already loaded, skipping initialization");
            return;
        }

        System.out.println("Loading comprehensive growth guides data (0-48 months)...");

        loadFirstYear(); // Months 0-11
        loadSecondYear(); // Months 12-23
        loadThirdYear(); // Months 24-35
        loadFourthYear(); // Months 36-47
        loadFourthBirthday(); // Month 48

        System.out.println("Growth guides data loaded successfully! Total: " + growthGuideRepository.count());
    }

    private void loadFirstYear() {
        // Month 0 (Birth - 1 Month)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("0-1 months")
                .ageDescription("Newborn")
                .physicalDevelopment(Arrays.asList(
                        "Birth weight typically 6-8 pounds, length 18-22 inches",
                        "Loses up to 10% of birth weight in first week, regains by 2 weeks",
                        "Sleeps 14-17 hours per day in 2-4 hour stretches",
                        "Primitive reflexes present: rooting, sucking, startle (Moro), grasp"))
                .cognitiveSocial(Arrays.asList(
                        "Focuses on objects 8-12 inches away (perfect for seeing parent's face during feeding)",
                        "Recognizes familiar voices and smells",
                        "Beginning to show preference for human faces",
                        "Cries to communicate needs (hunger, discomfort, tiredness)"))
                .motorSkills(Arrays.asList(
                        "Jerky, uncontrolled movements",
                        "Can briefly lift head when on tummy",
                        "Strong grasp reflex when objects placed in palm"))
                .build());

        // Month 1 (1-2 Months)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("1-2 months")
                .ageDescription("Early Infancy")
                .physicalDevelopment(Arrays.asList(
                        "Gains 1-2 pounds, grows 1-2 inches",
                        "Sleep patterns slightly more regular, 14-16 hours daily",
                        "Umbilical cord stump falls off",
                        "Vision improves, can see 12-18 inches clearly"))
                .cognitiveSocial(Arrays.asList(
                        "First social smiles appear (usually around 6-8 weeks)",
                        "More alert periods during the day",
                        "Begins to recognize primary caregivers",
                        "Crying becomes more purposeful and varied"))
                .motorSkills(Arrays.asList(
                        "Holds head up for short periods during tummy time",
                        "Movements become slightly less jerky",
                        "May briefly follow moving objects with eyes",
                        "Hands remain mostly fisted"))
                .build());

        // Month 2 (2-3 Months)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("2-3 months")
                .ageDescription("Growing Infant")
                .physicalDevelopment(Arrays.asList(
                        "Continues steady weight gain of 1-2 pounds",
                        "Sleep stretches may extend to 4-6 hours at night",
                        "Develops more regular feeding schedule"))
                .cognitiveSocial(Arrays.asList(
                        "Smiles responsively and intentionally",
                        "Makes cooing and gurgling sounds",
                        "Shows interest in surroundings",
                        "May calm when talked to or picked up",
                        "Begins to show different cries for different needs"))
                .motorSkills(Arrays.asList(
                        "Lifts head 45 degrees during tummy time",
                        "Follows objects with eyes in 180-degree arc",
                        "Brings hands together",
                        "Kicks legs vigorously when lying down"))
                .build());

        // Month 3 (3-4 Months)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("3-4 months")
                .ageDescription("Active Infant")
                .physicalDevelopment(Arrays.asList(
                        "Weight typically doubles from birth weight",
                        "Growth rate begins to slow slightly",
                        "Sleep may consolidate into longer nighttime periods"))
                .cognitiveSocial(Arrays.asList(
                        "Laughs out loud",
                        "Recognizes familiar faces and voices clearly",
                        "Shows excitement when seeing familiar people",
                        "Enjoys simple games like peek-a-boo",
                        "Responds to own name being called"))
                .motorSkills(Arrays.asList(
                        "Holds head steady when upright",
                        "Pushes up on forearms during tummy time",
                        "Reaches for and swats at dangling objects",
                        "Opens and closes hands voluntarily",
                        "May bring hands to mouth consistently"))
                .build());

        // Month 4 (4-5 Months)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("4-5 months")
                .ageDescription("Curious Explorer")
                .physicalDevelopment(Arrays.asList(
                        "Growth continues at steady pace",
                        "May show signs of teething preparation",
                        "Sleep patterns becoming more predictable"))
                .cognitiveSocial(Arrays.asList(
                        "Laughs heartily and shows joy",
                        "Initiates social interaction",
                        "Shows curiosity about environment",
                        "May show wariness of strangers",
                        "Babbles with single syllables"))
                .motorSkills(Arrays.asList(
                        "Rolls from tummy to back",
                        "Sits with support",
                        "Reaches for objects with better accuracy",
                        "Grasps objects and brings them to mouth",
                        "Pushes feet against surfaces when held standing"))
                .build());

        // Month 5 (5-6 Months)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("5-6 months")
                .ageDescription("Half Year Milestone")
                .physicalDevelopment(Arrays.asList(
                        "Weight gain may slow to 1 pound per month",
                        "Teething may begin (though timing varies widely)",
                        "Shows readiness for solid food introduction"))
                .cognitiveSocial(Arrays.asList(
                        "Shows clear preferences for certain people and toys",
                        "Responds to emotions in others' voices",
                        "Enjoys looking at self in mirror",
                        "May show anxiety when separated from primary caregiver"))
                .motorSkills(Arrays.asList(
                        "Rolls both ways (tummy to back and back to tummy)",
                        "Sits without support for brief periods",
                        "Transfers objects from one hand to another",
                        "Uses whole hand to grab objects (palmar grasp)",
                        "Bears weight on legs when supported"))
                .build());

        // Month 6 (6-7 Months)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("6-7 months")
                .ageDescription("Sitting Independently")
                .physicalDevelopment(Arrays.asList(
                        "Ready to start solid foods",
                        "May cut first teeth (usually bottom front teeth)",
                        "Growth rate continues to slow gradually"))
                .cognitiveSocial(Arrays.asList(
                        "Responds to own name consistently",
                        "Shows strong attachment to primary caregivers",
                        "Stranger anxiety may begin",
                        "Enjoys cause-and-effect toys",
                        "Makes sounds like 'ma,' 'ba,' 'da'"))
                .motorSkills(Arrays.asList(
                        "Sits without support for several minutes",
                        "Rocks back and forth on hands and knees",
                        "Uses raking motion to pick up small objects",
                        "Passes objects from hand to hand",
                        "Bounces when supported in standing position"))
                .build());

        // Month 7 (7-8 Months)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("7-8 months")
                .ageDescription("Mobile Explorer")
                .physicalDevelopment(Arrays.asList(
                        "Continues steady growth",
                        "More teeth may appear",
                        "Develops pincer grasp preparation",
                        "Shows clear food preferences"))
                .cognitiveSocial(Arrays.asList(
                        "Understands simple words like 'no' and 'bye-bye'",
                        "Shows fear of falling or heights (depth perception developing)",
                        "Plays simple games like peek-a-boo",
                        "May become clingy with primary caregivers"))
                .motorSkills(Arrays.asList(
                        "Sits steadily without support",
                        "Gets into crawling position and rocks",
                        "May begin to crawl (timing varies greatly)",
                        "Pulls self up to standing with furniture support",
                        "Claps hands together"))
                .build());

        // Month 8 (8-9 Months)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("8-9 months")
                .ageDescription("Independent Sitter")
                .physicalDevelopment(Arrays.asList(
                        "Growth continues at slower but steady pace",
                        "Hand-eye coordination improves significantly",
                        "Shows clear food likes and dislikes"))
                .cognitiveSocial(Arrays.asList(
                        "Stranger anxiety peaks",
                        "Shows separation anxiety",
                        "Understands object permanence",
                        "Responds to simple commands with gestures",
                        "Makes consonant-vowel combinations"))
                .motorSkills(Arrays.asList(
                        "Crawls (though some babies skip crawling entirely)",
                        "Pulls to standing position",
                        "Cruises along furniture",
                        "Uses thumb and finger to pick up small objects",
                        "Claps hands and waves bye-bye"))
                .build());

        // Month 9 (9-10 Months)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("9-10 months")
                .ageDescription("Almost Walking")
                .physicalDevelopment(Arrays.asList(
                        "Weight is typically triple birth weight",
                        "May have 2-4 teeth",
                        "Shows strong preferences for certain foods and textures"))
                .cognitiveSocial(Arrays.asList(
                        "Says 'mama' and 'dada' with meaning",
                        "Understands simple instructions",
                        "Shows preferences for certain activities and people",
                        "May point to objects of interest",
                        "Imitates simple sounds and actions"))
                .motorSkills(Arrays.asList(
                        "Crawls well and quickly",
                        "Stands while holding furniture",
                        "Sits down from standing position",
                        "Fine motor skills improve",
                        "May take first independent steps"))
                .build());

        // Month 10 (10-11 Months)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("10-11 months")
                .ageDescription("Pre-Walker")
                .physicalDevelopment(Arrays.asList(
                        "Growth rate continues to slow",
                        "More teeth may emerge",
                        "Shows independence in eating finger foods"))
                .cognitiveSocial(Arrays.asList(
                        "Follows simple one-step instructions",
                        "Shows understanding of cause and effect",
                        "Enjoys simple books with pictures",
                        "May say one or two words clearly",
                        "Shows empathy when others are upset"))
                .motorSkills(Arrays.asList(
                        "Cruises confidently along furniture",
                        "May stand alone for a few seconds",
                        "Walks when hands are held",
                        "Can climb stairs on hands and knees",
                        "Refined pincer grasp - picks up tiny objects"))
                .build());

        // Month 11 (11-12 Months)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("11-12 months")
                .ageDescription("Near First Birthday")
                .physicalDevelopment(Arrays.asList(
                        "Growth continues at steady but slower pace",
                        "May have 4-6 teeth",
                        "Shows readiness for transition to whole milk"))
                .cognitiveSocial(Arrays.asList(
                        "Vocabulary may include 2-3 clear words",
                        "Understands many more words than can say",
                        "Shows affection openly",
                        "May have temper tantrums when frustrated",
                        "Enjoys simple pretend play"))
                .motorSkills(Arrays.asList(
                        "May take first independent steps",
                        "Stands alone confidently",
                        "Squats down and stands back up",
                        "Can drink from a sippy cup",
                        "Scribbles with large crayons"))
                .build());
    }

    private void loadSecondYear() {
        // Month 12 (First Birthday)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("12 months")
                .ageDescription("First Birthday!")
                .physicalDevelopment(Arrays.asList(
                        "Typically weighs 3 times birth weight",
                        "May have 6-8 teeth",
                        "Can transition to whole milk",
                        "Sleep consolidates to 1-2 naps per day"))
                .cognitiveSocial(Arrays.asList(
                        "Says 2-6 meaningful words",
                        "Follows simple commands",
                        "Shows pride in accomplishments",
                        "May show defiant behavior",
                        "Enjoys music and dancing"))
                .motorSkills(Arrays.asList(
                        "Takes first independent steps",
                        "Can stand up from sitting without support",
                        "Throws objects intentionally",
                        "Can help with dressing",
                        "Stacks two blocks"))
                .build());

        // Months 13-23 (continuing second year)
        String[][] secondYearData = {
            {"13", "13 months", "Early Toddler",
                "Growth rate slows further|Appetite may decrease|Sleep stabilizes around 11-14 hours",
                "Vocabulary 5-10 words|Points to body parts|Shows jealousy|Seeks approval|Imitates household activities",
                "Walks with wide stance|Climbs onto furniture|Feeds self with spoon|Turns pages|Can squat to pick up toys"},
            {"14", "14 months", "Steady Walker",
                "Weight gain slows to 8 ounces per month|More molars may appear|Shows food preferences",
                "Uses 10-20 words|Follows two-step instructions|Shows affection to dolls|May begin parallel play|Expresses emotions clearly",
                "Walks steadily|Can walk backward|Climbs stairs with help|Stacks 3-4 blocks|Can remove simple clothing"},
            {"15", "15 months", "Independent Explorer",
                "Growth continues at slower pace|Transitioned to table foods|May resist nap times",
                "Vocabulary 20+ words|Uses words to make wants known|Shows independence|May have tantrums|Enjoys songs and rhymes",
                "Runs (though unsteady)|Walks up stairs with hand held|Kicks a ball|Draws scribbles|Drinks from open cup with help"},
            {"16", "16 months", "Growing Communicator",
                "Steady but slower growth|May have 12+ teeth|Shows strong food preferences",
                "Uses 20-50 words|Begins combining words|Shows increased independence|May resist help|Enjoys interactive games",
                "Runs more confidently|Climbs onto chairs|Throws ball overhand|Uses spoon and fork better|Helps with simple tasks"},
            {"17", "17 months", "Little Helper",
                "Growth rate continues to slow|Sleep needs 11-13 hours|Shows clear food preferences",
                "Vocabulary 50+ words|May put two words together|Shows empathy|Enjoys pretend play|May show possessiveness",
                "Walks up/down stairs with help|Runs without falling often|Climbs playground equipment|Can undress simple clothing|Scribbles with control"},
            {"18", "18 months", "Halfway to Two",
                "May weigh 4 times birth weight|Most teeth emerging|Shows strong independence",
                "Uses 50-100+ words|Combines words into phrases|Shows frustration when not understood|Parallel play evident|May show potty readiness",
                "Runs well and seldom falls|Walks up stairs holding railing|Jumps in place|Turns doorknobs|Builds 4+ block towers"},
            {"19", "19 months", "Curious Questioner",
                "Growth steady but slow|Sleep consolidates|Shows independence in self-care",
                "Vocabulary expanding|Uses 100+ words|Asks 'what's that?' frequently|Shows pride|May have increased tantrums",
                "Kicks ball standing|Walks backward confidently|Climbs well|Can partially dress self|Turns pages one at a time"},
            {"20", "20 months", "Active Learner",
                "Growth rate stabilizes|Most baby teeth present|Shows readiness for complex foods",
                "Vocabulary 150+ words|Combines words frequently|Understands big/little, hot/cold|Complex pretend play|May show jealousy",
                "Runs smoothly|Walks down stairs with help|Jumps with both feet|Can partially potty train|Draws circles and lines"},
            {"21", "21 months", "Social Butterfly",
                "Growth continues steadily|Sleep needs 11-13 hours|Shows independence in activities",
                "Uses 200+ words|Simple 2-3 word sentences|Follows two-step instructions|Shows social awareness|May begin cooperative play",
                "Can jump forward|Walks stairs alone (holding rail)|Can pedal riding toys|Shows hand preference|Completes 2-3 piece puzzles"},
            {"22", "22 months", "Independent Spirit",
                "Growth steady but slow|Most weigh 25-30 pounds|Shows readiness for independence",
                "Vocabulary 250+ words|Uses pronouns (me, you, mine)|Understands ownership|Complex pretend scenarios|May show defiance",
                "Jumps down from low heights|Climbs confidently|Kicks ball while running|Improved fine motor control|Helps with cooking"},
            {"23", "23 months", "Almost Two",
                "Approaches 30+ pounds|Sleep patterns established|Ready for toddler bed transition",
                "Uses 300+ words|Speaks in 2-4 word sentences|Understands more than expresses|Shows emotional complexity|May show potty readiness",
                "Runs, jumps, climbs confidently|Walks on tiptoes|Throws ball with aim|Turns pages consistently|Ready for tricycle"}
        };

        for (String[] data : secondYearData) {
            growthGuideRepository.save(GrowthGuide.builder()
                    .monthRange(data[1])
                    .ageDescription(data[2])
                    .physicalDevelopment(Arrays.asList(data[3].split("\\|")))
                    .cognitiveSocial(Arrays.asList(data[4].split("\\|")))
                    .motorSkills(Arrays.asList(data[5].split("\\|")))
                    .build());
        }
    }

    private void loadThirdYear() {
        // Month 24 (Second Birthday) and months 25-35
        String[][] thirdYearData = {
            {"24", "24 months", "Second Birthday!",
                "Weighs 26-32 pounds, height 32-36 inches|Has most baby teeth|Growth rate slower than first year",
                "Vocabulary 300-500+ words|Uses 3-4 word sentences|Begins asking why questions|Shows increased emotional regulation|Engages in parallel play",
                "Runs well without falling|Walks stairs alternating feet|Jumps with both feet off ground|Can ride tricycle|Builds 6+ block towers"},
            {"25", "25 months", "Two and Growing",
                "Growth continues steady|May show potty training readiness|Sleep needs 11-13 hours",
                "Vocabulary 500+ words|Uses possessive pronouns|Shows empathy consistently|Begins imaginative play|May show cooperation",
                "Stands on one foot briefly|Climbs jungle gyms|Can pedal tricycle|Shows improved balance|Draws vertical and horizontal lines"},
            {"26", "26 months", "Little Artist",
                "Weight gain 4-6 pounds/year|Height increases 2-3 inches/year|Shows increased independence",
                "Uses 500-700 words|Speaks in 4-5 word sentences|Understands complex instructions|Shows pride in accomplishments|Shows interest in other children",
                "Can hop on one foot|Throws ball with accuracy|Can catch large ball|Improved fine motor skills|Completes 4-6 piece puzzles"},
            {"27", "27 months", "Ready Learner",
                "Growth rate stabilizes|Many ready for potty training|Sleep patterns established",
                "Vocabulary 700+ words|Uses complex sentences|Asks why and what questions|Shows increased social interest|May begin turn-taking",
                "Walks on balance beam|Climbs stairs like adult|Jumps over small obstacles|Shows hand dominance|Draws recognizable shapes"},
            {"28", "28 months", "Cooperative Player",
                "Growth continues steadily|Many begin potty training|Shows readiness for independence",
                "Uses 700-1000 words|Tells simple stories|Understands emotions|Engages in cooperative play|May show jealousy",
                "Rides tricycle confidently|Jumps forward and backward|Walks on tiptoes consistently|Improved drawing skills|Helps with dressing completely"},
            {"29", "29 months", "Creative Thinker",
                "Weight 28-34 pounds|Height 34-38 inches|Shows increased appetite",
                "Vocabulary approaching 1000 words|Uses complex grammar|Understands time concepts|Engages in imaginative play|May show defiance",
                "Hops forward several times|Throws overhand with accuracy|Can catch bounced ball|Refined fine motor control|Can cut with child scissors"},
            {"30", "30 months", "Two and a Half",
                "Growth continues steady|Most potty trained during day|Sleep needs 10-12 hours plus nap",
                "Uses 1000+ words|Speaks in complete sentences|Understands rules|Engages in cooperative play|Shows emotional maturity",
                "Walks heel-to-toe|Runs around obstacles|Pedals tricycle around corners|Draws circles and crosses|Completes 8-10 piece puzzles"},
            {"31", "31 months", "Physical Achiever",
                "Growth rate steady|Ready for challenging activities|May transition from afternoon nap",
                "Vocabulary expanding|Uses complex sentences|Understands cause and effect|Elaborate pretend play|Shows increased social skills",
                "Balances on one foot 2-3 seconds|Climbs ladders|Throws to specific target|Improved pencil grip|Dresses mostly independently"},
            {"32", "32 months", "Story Teller",
                "Weight 30-36 pounds|Height increases steadily|Shows increased coordination",
                "Uses 1200+ words|Tells detailed stories|Understands past and future|Engages in group play|May show leadership",
                "Hops on one foot multiple times|Rides tricycle with steering|Can catch small ball|Draws recognizable pictures|Uses eating utensils properly"},
            {"33", "33 months", "Empathetic Friend",
                "Growth continues predictably|Most fully potty trained|Shows readiness for independence",
                "Vocabulary 1300+ words|Uses complex grammar|Understands others' emotions|Cooperates in groups|Shows increased empathy",
                "Walks down stairs alternating feet|Jumps from higher surfaces safely|Can ride scooter|Refined drawing skills|Completes 12+ piece puzzles"},
            {"34", "34 months", "Confident Explorer",
                "Growth rate stable|Shows increased physical confidence|May begin losing nap",
                "Uses 1400+ words|Engages in complex conversations|Understands rules and consequences|Plays cooperatively extended periods|Shows independence",
                "Can gallop|Climbs confidently|Throws and catches with accuracy|Draws people with features|Uses child tools effectively"},
            {"35", "35 months", "Almost Three",
                "Weighs 32-38 pounds|Height 36-40 inches|Shows adult-like proportions developing",
                "Vocabulary 1500+ words|Tells elaborate stories|Understands time sequences|Engages in collaborative play|Shows emotional regulation",
                "Can skip beginning steps|Rides tricycle with complex maneuvering|Can somersault|Shows mature pencil grip|Completes age-appropriate crafts"}
        };

        for (String[] data : thirdYearData) {
            growthGuideRepository.save(GrowthGuide.builder()
                    .monthRange(data[1])
                    .ageDescription(data[2])
                    .physicalDevelopment(Arrays.asList(data[3].split("\\|")))
                    .cognitiveSocial(Arrays.asList(data[4].split("\\|")))
                    .motorSkills(Arrays.asList(data[5].split("\\|")))
                    .build());
        }
    }

    private void loadFourthYear() {
        // Month 36 (Third Birthday) and months 37-47
        String[][] fourthYearData = {
            {"36", "36 months", "Third Birthday!",
                "Growth continues at preschool pace|Most fully potty trained|Shows readiness for preschool",
                "Uses 1500+ words|Speaks in complex sentences|Understands counting, colors, shapes|Cooperative and competitive play|Shows social awareness",
                "Alternates feet on stairs|Rides tricycle confidently|Can broad jump|Draws crosses and letters|Shows readiness for sports"},
            {"37", "37 months", "Preschool Ready",
                "Growth rate continues|Shows increased stamina|May begin transitioning from naps",
                "Vocabulary beyond 1500 words|Uses adult-like grammar|Understands social rules|Imaginative group play|Shows increased questions",
                "Hops on one foot consistently|Improved balance and coordination|Throws accurately|Beginning to write letters|Interest in complex building"},
            {"38", "38 months", "Problem Solver",
                "Weight 34-40 pounds|Shows increased strength|Sleep needs 10-12 hours",
                "Uses complex vocabulary|Detailed storytelling|Understands emotions and social cues|Cooperates in projects|Shows independence in problem-solving",
                "Marches in rhythm|Climbs and slides confidently|Catches ball consistently|Draws recognizable pictures|Ready for bicycle with training wheels"},
            {"39", "39 months", "Skilled Communicator",
                "Growth continues at preschool pace|Shows adult-like eating|May fully transition from naps",
                "Vocabulary exceeds 2000 words|Uses language for complex communication|Understands rules and fair play|Elaborate pretend scenarios|Shows empathy and social skills",
                "Skips with alternating feet|Shows improved sports skills|Uses scissors effectively|Beginning to write name|Coordination for dance and rhythm"},
            {"40", "40 months", "Team Player",
                "Weighs 36-42 pounds|Height 38-42 inches|Shows increased confidence",
                "Uses advanced vocabulary|Engages in complex problem-solving|Understands others' perspectives|Participates in rule-based games|May show leadership",
                "Can do forward rolls|Rides scooter or bike with training wheels|Accuracy in throwing and catching|Can draw detailed pictures|Ready for organized sports"},
            {"41", "41 months", "Creative Performer",
                "Growth continues steadily|Shows increased coordination|Fully transitioned to adult sleep patterns",
                "Vocabulary continues expanding|Understands complex concepts|Cooperative problem-solving|Shows increased social skills|Ready for structured learning",
                "Gallops smoothly|Improved balance on surfaces|Manipulates small objects precisely|Interest in writing letters and numbers|Coordination for instruments"},
            {"42", "42 months", "Academic Starter",
                "Weight 38-44 pounds|Shows continued growth|Demonstrates increased capabilities",
                "Uses sophisticated language|Understands cause and effect|Complex social interactions|Shows emotional maturity|Ready for pre-academic skills",
                "Performs complex climbing|Improved sports skills|Can draw letters and words|Precision in fine motor tasks|Ready for complex physical challenges"},
            {"43", "43 months", "Abstract Thinker",
                "Growth rate steady|Shows adult-like proportions|Demonstrates increased confidence",
                "Vocabulary exceeds 2500 words|Engages in abstract thinking|Understands complex social situations|Demonstrates independence|Ready for structured learning",
                "Performs standing broad jump|Mature running and jumping|Can write letters recognizably|Improved hand-eye coordination|Ready for team sports"},
            {"44", "44 months", "Kindergarten Prep",
                "Weighs 40-46 pounds|Height 40-44 inches|Shows continued growth",
                "Uses complex language structures|Understands time, numbers, abstract concepts|Engages in cooperative learning|Shows social competence|Ready for kindergarten prep",
                "Skip rope beginning attempts|Coordination for complex equipment|Can write name and simple words|Precision in cutting and pasting|Demonstrates sports skills"},
            {"45", "45 months", "Academic Enthusiast",
                "Growth continues at preschool pace|Shows increased stamina|Demonstrates adult-like coordination",
                "Vocabulary extensive and sophisticated|Understands complex rules|Detailed planning and problem-solving|Shows empathy and social awareness|Ready for academic challenges",
                "Performs complex physical sequences|Mature throwing and catching|Can write letters and numbers clearly|Artistic abilities in drawing and crafts|Coordination for musical activities"},
            {"46", "46 months", "Pre-Kindergarten Star",
                "Weight 42-48 pounds|Shows continued growth|Demonstrates increased capabilities",
                "Uses adult-like language complexity|Shows abstract thinking|Complex social problem-solving|Shows independence and responsibility|Ready for formal learning",
                "Rides bicycle with training wheels|Shows sports skills and teamwork|Can write simple sentences|Shows artistic creativity|Coordination for various activities"},
            {"47", "47 months", "Almost Four",
                "Approaching 4-year-old size|Shows continued growth|Demonstrates adult-like patterns",
                "Vocabulary and language well-developed|Ready for kindergarten concepts|Complex peer relationships|Shows emotional regulation|Demonstrates independence",
                "Shows mature gross motor skills|Performs complex fine motor tasks|Ready for writing and academics|Demonstrates sports competence|Shows creativity in expression"}
        };

        for (String[] data : fourthYearData) {
            growthGuideRepository.save(GrowthGuide.builder()
                    .monthRange(data[1])
                    .ageDescription(data[2])
                    .physicalDevelopment(Arrays.asList(data[3].split("\\|")))
                    .cognitiveSocial(Arrays.asList(data[4].split("\\|")))
                    .motorSkills(Arrays.asList(data[5].split("\\|")))
                    .build());
        }
    }

    private void loadFourthBirthday() {
        // Month 48 (Fourth Birthday)
        growthGuideRepository.save(GrowthGuide.builder()
                .monthRange("48 months")
                .ageDescription("Fourth Birthday!")
                .physicalDevelopment(Arrays.asList(
                        "Typically weighs 44-50 pounds",
                        "Height around 42-46 inches",
                        "Shows well-proportioned, coordinated body"))
                .cognitiveSocial(Arrays.asList(
                        "Uses sophisticated vocabulary (3000+ words)",
                        "Shows readiness for formal education",
                        "Engages in complex friendships",
                        "Shows emotional maturity appropriate for age",
                        "Demonstrates independence and responsibility"))
                .motorSkills(Arrays.asList(
                        "Shows mature movement patterns in all areas",
                        "Can perform complex physical and fine motor tasks",
                        "Shows readiness for academic writing and drawing",
                        "Demonstrates sports abilities and teamwork",
                        "Shows creativity and skill in various physical activities"))
                .build());
    }
}
