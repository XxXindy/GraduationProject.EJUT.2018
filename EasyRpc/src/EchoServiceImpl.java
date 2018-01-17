
public class EchoServiceImpl implements EchoService {

	@Override
	public String echo(String ping) {
		return ping != null ? ping + "--> I am OK." : " I am ok.";
	}

}
