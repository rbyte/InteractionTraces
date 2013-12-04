package matt.meta;

import java.lang.annotation.Documented;

@Documented
public @interface AuthorInformation {
	   String author() default "Matthias Graf";
	   String email() default "matthias.graf@eclasca.de";
	   String email2() default "matthias.graf@st.ovgu.de";
	   int yearCreated() default 2012;
}
