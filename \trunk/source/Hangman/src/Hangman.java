import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Hangman Spiel, Versuche 10. Expotiert Wörter aus hangmanwords.hangman (txt-Datei)
//Muster ist: WortWortWort..., split bei Großbuchstaben
public class Hangman
{
	public static void main(String[] args) throws IOException {
		
		@SuppressWarnings("unused")
		Hangman h = new Hangman();
	}
	public Hangman() throws IOException
	{
		System.out.println("Hangman, das Spiel! Spiele gegen mich, den ultimativen Wortwisser.");
		//Erstelle Wortliste aus Datei
		createWordlist();
		//Spielschleife um Wortliste nicht immer erneuern zu müssen
		while(true){
			//Starte das Spiel
			play();
			System.out.println("Noch ein Spiel? (y)");
			String input = new Scanner(System.in).next();
			if ( !(input.equals("y"))) {
				System.out.println("Schade. Na dann bis zum naechsten Mal!");
				//Programmende durch Benutzer
				break;
			}
			//Noch ein Spiel - Laufe Schleife erneut ab
			System.out.println("Super, noch ein Spiel!");
		}
	}
	//Startet das Hangman-Spiel, Wortliste muss bereits erstellt sein
	private void play() throws IOException
	{
		//Hole ein Wort
		String hangmanWord = getWord();
		//Wort kann null sein wenn Datei corrupt oder der RandomIndex auf EOF springt
		if (hangmanWord == null) {
			System.out.println(">Mir faellt kein Wort ein, versuche es nochmal oder hast du meine Woerterliste beschaedigt?");
			return;
		}
		//Wort in Ordnung - los gehts
		System.out.println(">Habe mir ein Wort ueberlegt.");
		System.out.println("Jetzt kanns losgehen!");
		//Benutzte Chars erstmal leer
		String usedChars = "";
		//Erratenes Wort derzeit noch leer
		String guessedWord = "";
		//Erstelle Hangman-Wortanzeige mit _
		for ( int i = 0; i < hangmanWord.length(); i++ )
			guessedWord += "_";
		//Spiel-Rateschleife
		for ( int guesses = 1; ; )
		{
			//Spielende nach 10 Fehlern
			if ( guesses == 10 )
			{
				System.out.printf( "Ich hab gewonnen! "+
                           "Achja, das Wort war '%s'.%n", hangmanWord );
				break;
			}
			System.out.printf( "Runde %d. Bisher geraten: %s. Was waehlst du fuer ein Zeichen?%n", guesses, guessedWord );
			//Einlesen des geratenen Zeichens
			char c = new java.util.Scanner( System.in ).next().charAt( 0 );
			//Falls Zeichen bereits verwendet wurde, setzte Versuche auserdem auch hoch
			if ( usedChars.indexOf( c ) >= 0 )
			{
				System.out.printf( "%c hast du schon mal getippt!%n", c );
    	    guesses++;
			}
			else  // Zeichen wurde noch nicht benutzt
			{
				//Füge Zeichen zu bereits-benutzt hinzu
				usedChars += c;
				//Zeichen ist im Wort
				if ( hangmanWord.indexOf( c ) >= 0 )
				{
					guessedWord = "";
					//Setze char an passende Stelle im Wort
					for ( int i = 0; i < hangmanWord.length(); i++ )
						guessedWord += usedChars.indexOf( hangmanWord.charAt( i ) ) >= 0 ?
                             hangmanWord.charAt( i ) : "_";
                             //Wort noch nicht fertig
                             if ( guessedWord.contains( "_" ) )
                            	 System.out.printf( "Gut geraten, '%s' gibt es im Wort. " +
                               "Aber es fehlt noch was!%n", c );
                             else
                             {
                            	 //Spielende, Wort eraten
                            	 System.out.printf( "Gratulation, du hast das Wort '%s' erraten!%n",
                               hangmanWord );
                            	 break;
                             }
				}
				else // Char nicht im Wort, erhöhe auserdem Versuche
				{
					System.out.printf( "Pech gehabt, %c kommt im Wort nicht vor!%n", c );
					guesses++;
				}
			}
		}
	}
  //Erstelle Wortliste aus Datei hangmanwords.hangman - kann je nach Grösse lang dauern
  private void createWordlist() throws IOException
  {
	System.out.println(">Erstelle Woerterliste aus Datei. Bitte warten, dies kann etwas dauern.");
	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("hangmanwords.hangman"))));
	//Erstmal Wortliste auf null
	this.wordList = null;
	
	//Lese die gesamte Datei in die wordList
	String line = br.readLine();
	while (line != null){
		this.wordList += line;
		line = br.readLine();
	}
	br.close();
	System.out.println(">Woerterliste erstellt.");
  }

  //Liest ein zufälliges Wort aus der Wörterliste aus
  private String getWord()
  {
	  System.out.println(">Ueberlege mir ein Wort.");
	  //Falls Wörterliste noch nicht erstellt gib null zurück
	  if ( this.wordList == null) return null;
	  
	  //Zufälliger Index welcher sich in der Wörterliste befindet
	  int randomIndex = (int) (Math.random() * (this.wordList.length() - 1));
	  //Erstelle substring ab diesem Index
	  String subWordList = this.wordList.substring(randomIndex);
	  //Untersuche substring nach dem Muster Großbuchstabe-Wort-Großbuchstabe
	  Matcher matcher = Pattern.compile("[A-Z].*?[A-Z]").matcher(subWordList);
	  
	  //Wort erstmal null falls er keinen Treffer landet
	  String hangmanWord = null;
	  
	  //Wenn das Muster passt gib das Wort klein geschrieben zurück, trenne vorher noch den letzten Großbuchstaben ab
	  if (matcher.find()) {
		  hangmanWord = matcher.group();
		  hangmanWord = hangmanWord.substring(0, (hangmanWord.length() - 1));
		  hangmanWord = hangmanWord.toLowerCase();
	  }

	  return hangmanWord;
  }
  
  //Beinhaltet alle Wörter der Datei, wird initialisiert mit createWordlist()
  private String wordList;
}