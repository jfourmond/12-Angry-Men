# 12-Angry-Men

Travail Dirigé d'IA développementale sous la direction de :
- [Dr. KABACHI Nadia](http://eric.ish-lyon.cnrs.fr/11-FR-membre-Nadia.KABACHI),

Par : 
- [FOURMOND Jérôme](https://github.com/jfourmond/)
- [LANGLOIS Aurelien]()

Dans le cadre de l'Unité d'Enseignement **Système Multi-Angents** du [Master 2 Informatique - Parcours Intelligence Artificielle](http://master-info.univ-lyon1.fr/IA/) de l'[Université Claude Bernard Lyon 1](http://www.univ-lyon1.fr/).

Langage :
- Java (framework [JADE](http://jade.tilab.com/))

---

## Simulation d’un protocole minimaliste du débat « négociation »

Ce projet se base sur la négociation sous forme de débat comme proposée dans le film “12 hommes en colère” de Sydney Lumet

## *12 hommes en colère*

Le film **"12 hommes en colère"** film met en scène, dans les Etats‐Unis des années 60, un procès où tout semble accuser un adolescent du meurtre de son père.
Dans un juré composé de douze hommes, onze sont convaincus de sa culpabilité. Cependant le douzième, lui a quelques doutes et estime qu’il est de leur devoir d’en discuter. Ce qui est obligatoire car le vote du jury doit être unanime. Un long débat commence, débat dans lequel le douzième juré s’efforce de convaincre les autres qu’il y a lieu d’avoir un doute à défaut d’être convaincu de l’innocence de l’adolescent.

C’est ce débat qui doit être modélisé et simulé pour parvenir à un accord commun satisfaisant tous les participants.

	java jade.boot -gui Jury1:agents.Jury1;Jury2:agents.Jury2;Jury3:agents.Jury3;Jury4:agents.Jury4;Jury5:agents.Jury5;Jury6:agents.Jury6;Jury7:agents.Jury7;Jury8:agents.Jury8;Jury9:agents.Jury9;Jury10:agents.Jury10;Jury11:agents.Jury11;Jury12:agents.Jury12;