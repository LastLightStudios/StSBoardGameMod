package BoardGame.cards.BGRed;


 import BoardGame.powers.BGJuggernautPower;
 import BoardGame.characters.BGIronclad;
import BoardGame.cards.AbstractBGCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
 import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
 import com.megacrit.cardcrawl.cards.AbstractCard;
 import com.megacrit.cardcrawl.characters.AbstractPlayer;
 import com.megacrit.cardcrawl.core.AbstractCreature;
 import com.megacrit.cardcrawl.core.CardCrawlGame;
 import com.megacrit.cardcrawl.localization.CardStrings;
 import com.megacrit.cardcrawl.monsters.AbstractMonster;
 import com.megacrit.cardcrawl.powers.AbstractPower;

public class BGJuggernaut extends AbstractBGCard {
        private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("BoardGame:BGJuggernaut");
        public static final String ID = "BGJuggernaut";

        public BGJuggernaut() {
            super("BGJuggernaut", cardStrings.NAME, "red/power/juggernaut", 2, cardStrings.DESCRIPTION, AbstractCard.CardType.POWER, BGIronclad.Enums.BG_RED, AbstractCard.CardRarity.RARE, AbstractCard.CardTarget.SELF);










            this.baseMagicNumber = 1;
            this.magicNumber = this.baseMagicNumber;
        }


        public void use(AbstractPlayer p, AbstractMonster m) {
            addToBot((AbstractGameAction)new ApplyPowerAction((AbstractCreature)p, (AbstractCreature)p, (AbstractPower)new BGJuggernautPower((AbstractCreature)p, this.magicNumber,(AbstractCreature)m), this.magicNumber));
        }


        public void upgrade() {
            if (!this.upgraded) {
                upgradeName();
                upgradeMagicNumber(1);
            }
        }


        public AbstractCard makeCopy() {
            return new BGJuggernaut();
        }
    }




