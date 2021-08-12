package gambist.bo

import gambist.Bet
import gambist.BetService
import gambist.BetType
import gambist.Category
import gambist.CategoryService
import gambist.Match
import gambist.MatchService
import gambist.Team
import gambist.TeamService
import gambist.UserService
import gambist.Users

import java.sql.Timestamp

class BootStrap {

    CategoryService categoryService
    UserService userService
    MatchService matchService
    BetService betService
    TeamService teamService

    def init = { servletContext ->
//        JSON.registerObjectMarshaller(Date) {
//            return it?.format("dd.MM.yyyy")
//        }
        def users = createUsers()
        def categories = createCategories()
        def footballTeams = createFootballTeam(categories[0])
        def footballMatches = createMatches(footballTeams, categories[0], 10, 6)
        def bets = createBets(footballMatches, users)
        def basketballTeams = createBasketballTeam(categories[1])
        def basketballMatches = createMatches(basketballTeams, categories[1], 15, 10, 50)
        def basketballBets = createBets(basketballMatches, users)
        def volleyballTeams = createVolleyBallTeam(categories[2])
        def volleyballMatches = createMatches(volleyballTeams, categories[2], 10, 5, 5)
        def volleyballBets = createBets(volleyballMatches, users)
        def rugbyTeams = createRugbyTeam(categories[3])
        def rugbyMatches = createMatches(rugbyTeams, categories[3], 15, 7, 50)
        def rugbyBets = createBets(rugbyMatches, users)
        footballMatches.addAll(createMatches(footballTeams, categories[0], 10, 6))
    }

    private List<Category> createCategories() {
        println("Create categories")
        if(categoryService.count() > 0) return categoryService.list()
        def categoryName = ["Football", "Basketball", "Volleyball", "Rugby"]
        def categories = []
        categoryName.each {
            categories.add(categoryService.save(new Category(
                    label: it
            )))
        }
        return categories
    }

    private List<Bet> createBets(List<Match> matches, List<Users> users) {
        println("Create bets")
        if(betService.count() > 0) return betService.list()
        def bets = []
        matches.each { m ->
            Set<Integer> index = []
            8.times {
                index.add(randBetween(0, users.size()-1))
            }
            index.each { userIndex ->
                int j = 24 * 60 * 60 * 1000
                int rand = randBetween(3, 15)
                def selectedTeam = rand % 3 == 0 ? m.teamA : rand % 5 == 0 ? null : m.teamB
                def teamOdds = rand % 3 == 0 ? m.oddsA : rand % 5 == 0 ? m.oddsNul : m.oddsB
//                println(teamOdds + " - " + m.oddsA + ','+m.oddsNul+','+m.oddsB)
                def date = new java.sql.Date(System.currentTimeMillis() + (j * randBetween(0, 2)))
                bets.add(betService.save(new Bet(
                        user: users[userIndex],
                        match: m,
                        betDate: date,
                        winningRate: 0,
                        betValue: randBetween(2, 20) * 10,
                        team: selectedTeam,
                        odds: teamOdds
                )))
            }
            def twoHour = 7200000
            if((new Date().time-m.matchDate.time) >= twoHour)
                matchService.endMatch(m.id)
        }
    }

    private List<Users> createUsers() {
        println("Create user ")
        if(Users.count() > 0) return Users.findAll()
        def firstname = ["Johnatan", "Michael", "Andy", "Jean", "Hillary","Marena","Vinita","Tessy","Florenza","Nicky","Mella","Alissa","Giordano","Kakalina","Rhianon","Bobette","Willabella","Aime","Adrea","Christean","Gaylord","Kiah","Nalani","Honoria","Willis","Aeriell"]
        def lastname = ["Manandraibe", "Ramaroson", "Randrianirina", "Ianiello","Date","Mulvagh","Ceschini","Tolchar","Kerr","Goble","Kuhle","Macguire","Masding","Croose","Broker","Phillimore","Sawl","Dearl","Cowderoy","Broderick","Ubach","Crossley","Yoakley","Gorthy","Braisted", "Bell"]
        def users = []
        firstname.size().times {
            def user = userService.save(new Users(
                    email: "${firstname[it].toLowerCase()}@gmail.com",
                    password: 'password'.sha256(),
                    username: firstname[it],
                    dayOfBirth: randomDayOfBirth(),
                    firstname: firstname[it],
                    lastname: lastname[it],
                    bankBalance: 50
            ))
            users.add(user)
        }
        return users
    }

    private Date randomDayOfBirth() {
        GregorianCalendar gc = new GregorianCalendar();
        int year = randBetween(1970, 2002);
        gc.set(GregorianCalendar.YEAR, year);
        int dayOfYear = randBetween(1, gc.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));
        int month = randBetween(1, 12)
        gc.set(GregorianCalendar.MONTH, month)
        gc.set(gc.DAY_OF_YEAR, dayOfYear);
        return new Date(gc.getTimeInMillis())
    }

    private List<BetType> createFootballBetType(Category category) {
        def betTypeLabel = ["Ecart de score", "Victoire"]
        def betType = []
        betTypeLabel.each {
            betType.add(new BetType(
                    label: it,
                    category: category
            ).save())
        }
        return betType
    }

    private List<Team> createFootballTeam(Category footballCategory) {
        println("Create football teams")
        def footballTeams = []
        footballTeams = Team.findAllByCategory(footballCategory)
        if(!footballTeams.isEmpty()) return footballTeams
        footballTeams = []
        def footballTeams_ = [
                "Borussia Dortmound":"https://www.pngitem.com/pimgs/m/35-350698_transparent-bvb-logo-png-borussia-dortmund-logo-png.png",
                "Chelsea":"https://i.pinimg.com/736x/b9/22/c4/b922c4e18a85eeb707bf73423033442b.jpg",
                "PSG":"https://lh3.googleusercontent.com/dtFuCbfBxODq263Ramrmu-7jXxjsdL2YdyXA243PtwLr2U5xOAaUi63FwSgDRKuNTXCyPEyghjW-D2EVlfjnp4HU",
                "Manchester City":"https://lh3.googleusercontent.com/KNyKMfQqqVcLYAROYJ6KPW7nqmyMMcuc7npdzuzYI9KXhnZDJ3Wkfqy_apcQTDgq2QlNp9LzqQly06N5qsNxUOLT",
                "FC Barcelone":"https://lh3.googleusercontent.com/OQZi4ckWAs7UrOlZEPefXZgJOcdJuSM5FSH9zqD5rMg6c2MOaxcKpV5IMrb1Tju98fWyNmcI33E4RGb0uC09Ej4W",
                "Bayern":"https://cdn.1min30.com/wp-content/uploads/2018/03/Logo-Bayern-Munich-1.jpg",
                "Real Madrid":"https://upload.wikimedia.org/wikipedia/fr/thumb/c/c7/Logo_Real_Madrid.svg/1200px-Logo_Real_Madrid.svg.png",
                "Juventus":"https://www.ecofoot.fr/wp-content/uploads/2017/01/nouveau-logo-juventus.jpg",
                "Inter Milan":"https://assets-fr.imgfoot.com/media/cache/1200x900/nouveau-logo-inter-milan-img1.jpg",
                "Liverpool":"https://kgo.googleusercontent.com/profile_vrt_raw_bytes_1587515361_10542.jpg",
                "Atlético de Madrid":"https://upload.wikimedia.org/wikipedia/fr/thumb/9/93/Logo_Atl%C3%A9tico_Madrid_2017.svg/1200px-Logo_Atl%C3%A9tico_Madrid_2017.svg.png",
                "FC Porto":"https://upload.wikimedia.org/wikipedia/fr/thumb/6/65/FC_Porto_Vitalis_logo.svg/1200px-FC_Porto_Vitalis_logo.svg.png",
                "Ajax Amsterdam":"https://upload.wikimedia.org/wikipedia/fr/7/77/Ajax_Amsterdam_Logo.svg",
                "Tottenham":"https://kgo.googleusercontent.com/profile_vrt_raw_bytes_1587515401_10891.jpg"
        ]
        def keys = footballTeams_.keySet()
        keys.each {
            footballTeams.add(teamService.save(new Team(
                    name: it,
                    category: footballCategory,
                    logo: footballTeams_[it]
            )))
        }
        return footballTeams
    }

    private List<Team> createBasketballTeam(Category category) {
        println("Create basketball team")
        def basketBallTeams = []
        basketBallTeams = Team.findAllByCategory(category)
        if(!basketBallTeams.isEmpty()) return basketBallTeams
        basketBallTeams = []
        def basketBallTeams_ = [
                "USA Basketball":"https://upload.wikimedia.org/wikipedia/commons/6/6d/USA_Basketball_logo.svg",
                "Celtics de Boston":"https://upload.wikimedia.org/wikipedia/fr/thumb/6/65/Celtics_de_Boston_logo.svg/1200px-Celtics_de_Boston_logo.svg.png",
                "Nets de Brooklyn":"https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Brooklyn_Nets_newlogo.svg/1200px-Brooklyn_Nets_newlogo.svg.png",
                "Warriors de Golden State":"https://upload.wikimedia.org/wikipedia/fr/0/07/Warriors_de_Golden_State_logo_2019.png",
                "Bucks de Milwaukee":"https://upload.wikimedia.org/wikipedia/fr/3/34/Bucks2015.png",
                "Clippers de Los Angeles":"https://upload.wikimedia.org/wikipedia/fr/thumb/c/cb/Clippers_de_Los_Angeles_logo.svg/1200px-Clippers_de_Los_Angeles_logo.svg.png",
                "Suns de Phoenix":"https://lh3.googleusercontent.com/hsxmBOBzhgN99Fwxc6yMIm1mo8PfUJrslxwz3tyK9jinFS9hjEVLBo0o50lYkVl_Hm8CfuImvfnYnYOFKOR4sas",
                "Hawks d'Atlanta":"https://lh3.googleusercontent.com/UHeqkFiC7aSZBcpHI9TaSNautpfFOO34xxujafZOBgsuiW3dgZpOxJEgM0j3pAd6GDnNwMEf1jhuOy_gmEoeR1OXwA",
                "Cavaliers de Cleveland":"https://upload.wikimedia.org/wikipedia/fr/0/06/Cavs_de_Cleveland_logo_2017.png",
                "Lakers de Los Angeles":"https://i.pinimg.com/originals/c4/83/e5/c483e5839600506bf18e851421c88094.jpg",
                "Bulls de Chicago":"https://i.pinimg.com/originals/e1/b5/9f/e1b59f59d7512de49624b79cf382a95f.jpg",
                "Knicks de New York":"https://lezebre.lu/images/detailed/21/Sticker_new_york_knicks_logo.png",
                "Raptors de Toronto":"https://upload.wikimedia.org/wikipedia/fr/b/bd/Toronto_Raptors_current_logo.gif",
                "Pelicans de La Nouvelle-Orléans":"https://upload.wikimedia.org/wikipedia/fr/thumb/2/21/New_Orleans_Pelicans.png/200px-New_Orleans_Pelicans.png",
                "Pistons de Détroit":"https://upload.wikimedia.org/wikipedia/commons/6/6a/Detroit_Pistons_primary_logo_2017.png",
                "Trail Blazers de Portland":"https://upload.wikimedia.org/wikipedia/fr/thumb/6/68/Trail_Blazers_de_Portland_2017.png/200px-Trail_Blazers_de_Portland_2017.png",
                "Spurs de San Antonio":"https://upload.wikimedia.org/wikipedia/fr/0/0e/San_Antonio_Spurs_2018.png"
        ]
        def keys = basketBallTeams_.keySet()
        keys.each {
            basketBallTeams.add(teamService.save(new Team(
                    name: it,
                    category: category,
                    logo: basketBallTeams_[it]
            )))
        }
        return basketBallTeams
    }

    private List<Team> createVolleyBallTeam(Category category) {
        println("Create volleyball team")
        def team = []
//        team = Team.findAllByCategory(category)
//        if(!team.isEmpty()) return team
//        team = []
        def teams_ = [
                "Équipe du Japon de volley-ball":"https://c0.lestechnophiles.com/www.numerama.com/wp-content/uploads/2017/04/japan_volleyball_tournament.jpeg?resize=1212,712",
                "Équipe du Canada de volley-ball":"https://volleyball.ca/assets/images/share_facebook.jpg",
                "Équipe de Thaïlande féminine de volley-ball":"https://c8.alamy.com/compfr/p29bx3/eboli-italie-13-juin-2018-match-volley-femme-bresil-vs-thailande-volleyball-fivb-2018-league-women-nations-unies-en-eboli-salerno-italie-resultat-final-bresil-vs-thailande-3-1-25-16-25-22-18-25-25-13-dans-l-equipe-de-thailande-photo-credit-salvatore-esposito-pacific-press-alamy-live-news-p29bx3.jpg",
                "Équipe de Turquie de volley-ball":"https://www.fivb.org/Vis2009/Images/GetImage.asmx?No=201713942&width=1500&height=865&stretch=uniform",
                "Équipe de Belgique masculine de volley-ball":"https://ds1.static.rtbf.be/article/image/1248x702/b/2/f/60106888f8977b71e1f15db7bc9a88d1-1504254883.jpg",
                "Équipe de Serbie de volley-ball":"https://c8.alamy.com/compfr/ca3mk6/l-equipe-masculine-de-volleyball-de-la-serbie-groupe-srb-1-juin-2012-volley-ball-le-monde-fivb-hommes-tournoi-de-qualification-olympique-pour-les-jeux-olympiques-de-londres-en-2012-entre-le-japon-0-3-serbie-au-tokyo-metropolitan-gymnasium-tokyo-japon-photo-de-jun-tsukida-aflo-sport-0003-ca3mk6.jpg",
                "Équipe du Brésil de volley-ball":"https://gtimg.tokyo2020.org/image/private/f_auto/v1612143134/production/qsphzh2ubizgbyaqpxuk",
                "Équipe d'Espagne de volley-ball":"https://upload.wikimedia.org/wikipedia/commons/thumb/c/cb/Selecci%C3%B3n_masculina_de_voleibol_de_Espa%C3%B1a_-_01.jpg/300px-Selecci%C3%B3n_masculina_de_voleibol_de_Espa%C3%B1a_-_01.jpg",
                "Équipe de Grèce de volley-ball":"https://images.squarespace-cdn.com/content/v1/560195f3e4b0fcc5265b7b78/1479874088547-P9X6TR22AVBAXRP89S0Z/ke17ZwdGBToddI8pDm48kGlliuM0VlV6myF2u9wiGPJZw-zPPgdn4jUwVcJE1ZvWQUxwkmyExglNqGp0IvTJZUJFbgE-7XRK3dMEBRBhUpxfcFnw69gqYfE4QfiYxtS8lX6ZwI6iuVEvbAbL1_RlxW0Y3iM1lJB7Z_FqoRrWoxI/image-asset.jpeg",
                "Équipe des Pays-Bas de volley-ball":"https://www.fivb.org/Vis2009/Images/GetImage.asmx?No=201708091&width=1500&height=865&stretch=uniform",
                "Équipe d'Ukraine de volley-ball":"https://www.fivb.org/Vis2009/Images/GetImage.asmx?No=201724917&width=1500&height=865&stretch=uniform",
                "Équipe de Grande‑Bretagne":"https://upload.wikimedia.org/wikipedia/en/thumb/4/43/Britishvolleyball_logo.png/180px-Britishvolleyball_logo.png"
        ]
        def keys = teams_.keySet()
        keys.each {
            team.add(teamService.save(new Team(
                    name: it,
                    category: category,
                    logo: teams_[it]
            )))
        }
        return team
    }

    private List<Team> createRugbyTeam(Category category) {
        println("Create rugby team")
        def team = []
        team = Team.findAllByCategory(category)
        if(!team.isEmpty()) return team
        team = []
        def teams_ = [
            "Équipe des Lions britanniques": "https://upload.wikimedia.org/wikipedia/fr/7/73/Logo_Lions_britanniques_et_irlandais.png",
            "Équipe d'Angleterre": "https://upload.wikimedia.org/wikipedia/fr/thumb/c/cf/Logo_Rugby_Angleterre.svg/1200px-Logo_Rugby_Angleterre.svg.png",
            "Équipe de Nouvelle-Zélande": "https://upload.wikimedia.org/wikipedia/fr/thumb/b/b1/Logo_Rugby_Nouvelle-Z%C3%A9lande.svg/1200px-Logo_Rugby_Nouvelle-Z%C3%A9lande.svg.png",
            "Équipe d'Australie": "https://upload.wikimedia.org/wikipedia/fr/thumb/2/23/Logo_Wallabies.svg/1200px-Logo_Wallabies.svg.png",
            "Équipe d'Irlande": "https://upload.wikimedia.org/wikipedia/fr/thumb/3/31/Logo_Irish_Rugby_Football_Union_2009.svg/1200px-Logo_Irish_Rugby_Football_Union_2009.svg.png",
            "Équipe des États-Unis": "https://upload.wikimedia.org/wikipedia/fr/thumb/b/bf/Logo_USA_Rugby.svg/1200px-Logo_USA_Rugby.svg.png",
            "Équipe du pays de Galles": "https://upload.wikimedia.org/wikipedia/fr/thumb/6/6a/WRU_2016.svg/1200px-WRU_2016.svg.png",
            "Équipe d'Afrique du Sud": "https://upload.wikimedia.org/wikipedia/fr/thumb/e/e6/Bok_Logo.svg/1200px-Bok_Logo.svg.png",
            "Équipe d'Écosse": "https://upload.wikimedia.org/wikipedia/fr/thumb/b/b0/Scottish_Rugby_team_logo.svg/1200px-Scottish_Rugby_team_logo.svg.png",
            "Équipe d'Argentine": "https://upload.wikimedia.org/wikipedia/fr/7/7b/Logo_Los_Pumas.png",
            "Équipe de Géorgie": "https://upload.wikimedia.org/wikipedia/fr/2/2f/%E1%B2%A1%E1%83%90%E1%83%A5%E1%83%90%E1%83%A0%E1%83%97%E1%83%95%E1%83%94%E1%83%9A%E1%83%9D%E1%83%A1_%E1%83%A0%E1%83%90%E1%83%92%E1%83%91%E1%83%98%E1%83%A1_%E1%83%99%E1%83%90%E1%83%95%E1%83%A8%E1%83%98%E1%83%A0%E1%83%98_%28logo_2016%29.svg",
            "Équipe des Samoa": "https://upload.wikimedia.org/wikipedia/fr/thumb/3/35/Logo_Samoa_Rugby.svg/948px-Logo_Samoa_Rugby.svg.png",
            "Équipe des Fidji": "https://upload.wikimedia.org/wikipedia/fr/thumb/d/da/Logo_Flying_Fijians_2019.svg/langfr-110px-Logo_Flying_Fijians_2019.svg.png",
            "Équipe d'Italie": "https://upload.wikimedia.org/wikipedia/fr/thumb/e/e0/Italie_Rugby.svg/1200px-Italie_Rugby.svg.png",
            "Équipe du Japon": "https://upload.wikimedia.org/wikipedia/fr/thumb/3/37/Logo_JRFU.svg/1200px-Logo_JRFU.svg.png",
            "Équipe de Hong Kong": "https://upload.wikimedia.org/wikipedia/en/6/69/Hong_Kong_national_rugby_union_team_logo.png"
        ]
        def keys = teams_.keySet()
        keys.each {
            team.add(teamService.save(new Team(
                    name: it,
                    category: category,
                    logo: teams_[it]
            )))
        }
        return team
    }

    private List<Match> createMatches(List<Team> teams, Category category, int outdatedCount, int upcomingCount, maxScore=5) {
        println("Create match")
        def matches = []
        matches = Match.findAllByCategory(category)
        if(!matches.isEmpty()) return matches
        matches = []
        def random = new Random()
        def previousIndex = 0 // Pour ne pas avoir les memes equipes dans 2 match consecutif
        outdatedCount.times {
            def indexList = createArrayIndex(teams.size())
            int indexA = indexList.remove(random.nextInt(indexList.size()))
            int indexB = indexList.remove(random.nextInt(indexList.size()))
            double oddsA = random.nextDouble() + random.nextInt(5)
            oddsA = oddsA < 1 ? oddsA + 1 : oddsA
            double oddsB = random.nextDouble() + random.nextInt(oddsA >= 4 ? 2 : 5)
            oddsB = oddsB < 1 ? oddsB + 1 : oddsB
            double oddsNul = Math.abs(oddsB-oddsA)+1
            long time = new Date().getTime() - 72000000/2 * it
            int scoreA = random.nextInt(maxScore)
            matches.add(matchService.save(new Match(
                    teamA: teams[indexA],
                    teamB: teams[indexB],
                    category: category,
                    oddsA: oddsA,
                    oddsB: oddsB,
                    scoreA: scoreA,
                    scoreB: maxScore-scoreA,
                    oddsNul: oddsNul,
                    matchDate: new Timestamp(time)
            )))
        }
        previousIndex = 0
        def i = 0;
        upcomingCount.times {
            def indexList = createArrayIndex(teams.size())
            int indexA = indexList.remove(random.nextInt(indexList.size()))
            int indexB = indexList.remove(random.nextInt(indexList.size()))
            double oddsA = random.nextDouble() + random.nextInt(5)
            oddsA = oddsA < 1 ? oddsA + 1 : oddsA
            double oddsB = random.nextDouble() + random.nextInt(oddsA >= 4 ? 2 : 5)
            oddsB = oddsB < 1 ? oddsB + 1 : oddsB
            double oddsNul = Math.abs(oddsB-oddsA)+1
            long time = new Date().getTime() + 7200000 * (i++ < 2 ? 1 : it)
            matches.add(matchService.save(new Match(
                    teamA: teams[indexA],
                    teamB: teams[indexB],
                    category: category,
                    oddsA: oddsA,
                    oddsB: oddsB,
                    oddsNul: oddsNul,
                    matchDate: new Timestamp(time)
            )))
        }
        return matches
    }

    private int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

    private List<Integer> createArrayIndex(int size) {
        def array = new ArrayList()
        size.times {
            array.add(it)
        }
        return array
    }

    def destroy = {
    }
}
