package sernet.verinice.service.crypto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyAgreeRecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.JCERSAPrivateCrtKey;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.encoders.Base64;

import sernet.verinice.interfaces.encryption.EncryptionException;

/**
 * Abstract utility class providing static methods for S/MIME based encryption.
 * 
 * <p>
 * S/MIME stands for Secure/Multipurpose Internet Mail Extensions. As defined in
 * <a href="http://tools.ietf.org/html/rfc3851">RFC 2898</a>, <quote>"S/MIME
 * provides a consistent way to send and receive secure MIME data. Based on the
 * popular Internet MIME standard, S/MIME provides the following cryptographic
 * security services for electronic messaging applications: authentication,
 * message integrity and non-repudiation of origin (using digital signatures),
 * and data confidentiality (using encryption)"</quote>.
 * </p>
 * 
 * <p>
 * To encrypt a message (or other data) the public key certificate of the
 * receiver is required. Public key certificates prove that that the public key
 * it contains belongs to a certain identity (person, organisation, etc.). A
 * standard for public key certificates is X.509. This standard specifies that
 * the certificate content is definied in ASN.1. <br/>
 * The two main certificate formats are DER and PEM. The DEM format is a DER
 * (Distingushed Encoding Rules) encoded form of the ASN.1 certificate
 * definition. PEM is a Base64 encoded form of the DEM format with additional
 * ASCII header and footer, which include the encoded content.
 * </p>
 * 
 * <p>
 * -----BEGIN CERTIFICATE----- <br/>
 * Base64 encoded DER certificate content <br/>
 * Base64 encoded DER certificate content <br/>
 * Base64 encoded DER certificate content <br/>
 * -----END CERTIFICATE-----
 * </p>
 * 
 * @author Sebastian Engel <s.engel@tarent.de>
 * 
 */
public class SMIMEBasedEncryption {

    private static final String CRYPT_PROVIDER = "VeriniceSecurityProvider";
    private static final String CRYPT_TYPE = "PKCS11";
    private static final String STD_ERR_MSG = "There was a problem during the en- or decryption process. See the stacktrace for details.";
    private static final String IO_ERR_MSG = "There was an IO problem during the en- or decryption process. See the stacktrace for details.";
    private static final Logger log = Logger.getLogger(SMIMEBasedEncryption.class);

    private SMIMEBasedEncryption() {
    }

    /**
     * Encrypts the given byte data with the given X.509 certificate file.
     * 
     * Since encryption is realized through S/MIME, the public certificate of
     * the "receiver" is required. The certificate is expected to be in DER or
     * PEM format.
     * 
     * 
     * @param unencryptedByteData
     *            an array of byte data to encrypt
     * @param x509CertificateFile
     *            X.509 certificate file used to encrypt the data. The file is
     *            expected to be in DER or PEM format
     * @return an array of bytes representing a MimeBodyPart that contains the
     *         encrypted content
     * @throws IOException
     *             <ul>
     *             <li>if any of the given files does not exist</li>
     *             <li>if any of the given files cannot be read</li>
     *             </ul>
     * @throws CertificateNotYetValidException
     *             if the certificate is not yet valid
     * @throws CertificateExpiredException
     *             if the certificate is not valid anymore
     * @throws CertificateException
     *             <ul>
     *             <li>if the given certificate file does not contain a
     *             certificate</li>
     *             <li>if the certificate contained in the given file is not a
     *             X.509 certificate</li>
     *             </ul>
     * @throws EncryptionException
     *             if a problem occured during the encryption process
     */
    public static byte[] encrypt(byte[] unencryptedByteData, File x509CertificateFile)
            throws IOException, CertificateException, EncryptionException {

        byte[] encryptedMimeData = null;

        X509Certificate x509Certificate = CertificateUtils
                .loadX509CertificateFromFile(x509CertificateFile);
        try {
            CMSEnvelopedDataStreamGenerator edGen = new CMSEnvelopedDataStreamGenerator();
            edGen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(x509Certificate)
                    .setProvider(BouncyCastleProvider.PROVIDER_NAME));

            byte[] unencryptedByteData_0 = Base64.encode(unencryptedByteData);

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            OutputStream out = edGen.open(bout,
                    new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_CBC)
                            .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            DEROutputStream dos = new DEROutputStream(out);
            // Finally get the encoded bytes from the MimeMessage and return
            // them
            out.write(unencryptedByteData_0);
            out.close();
            encryptedMimeData = bout.toByteArray();

        } catch (GeneralSecurityException e) {
            log.error(STD_ERR_MSG, e);
            throw new EncryptionException(STD_ERR_MSG, e);
        } catch (CMSException e) {
            log.error(IO_ERR_MSG, e);
            throw new EncryptionException(IO_ERR_MSG, e);
        } catch (IllegalArgumentException e) {
            log.error(IO_ERR_MSG, e);
            throw new EncryptionException(IO_ERR_MSG, e);
        } catch (OperatorCreationException e) {
            log.error(IO_ERR_MSG, e);
            throw new EncryptionException(IO_ERR_MSG, e);
        } catch (Exception e) {
            log.error(STD_ERR_MSG, e);
            throw new EncryptionException(STD_ERR_MSG, e);
        } catch (Throwable t) {
            log.error(STD_ERR_MSG, t);
            throw new EncryptionException(STD_ERR_MSG, t);
        }
        encryptedMimeData = (encryptedMimeData == null) ? new byte[] {} : encryptedMimeData;
        return encryptedMimeData;
    }

    public static byte[] encrypt(byte[] unencryptedByteData, String keyAlias)
            throws IOException, CertificateException, EncryptionException {

        byte[] encryptedMimeData;

        try {
            KeyStore ks = KeyStore.getInstance(CRYPT_TYPE, CRYPT_PROVIDER);
            ks.load(null, null);
            X509Certificate cert = (X509Certificate) ks.getCertificate(keyAlias);

            SMIMEEnvelopedGenerator generator = new SMIMEEnvelopedGenerator();
            generator.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(cert));
            // .setProvider("SunPKCS11-verinice")
            byte[] unencryptedByteData_0 = Base64.encode(unencryptedByteData);
            MimeBodyPart unencryptedContent = SMIMEUtil.toMimeBodyPart(unencryptedByteData_0);

            // Encrypt the byte data and make a MimeBodyPart from it
            MimeBodyPart encryptedMimeBodyPart = generator.generate(unencryptedContent,
                    new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_CBC)
                            .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());

            // Finally get the encoded bytes from the MimeMessage and return
            // them
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            encryptedMimeBodyPart.writeTo(byteOutStream);
            encryptedMimeData = byteOutStream.toByteArray();

        } catch (GeneralSecurityException e) {
            throw new EncryptionException(STD_ERR_MSG, e);
        } catch (SMIMEException smimee) {
            throw new EncryptionException(STD_ERR_MSG, smimee);
        } catch (MessagingException e) {
            throw new EncryptionException(STD_ERR_MSG, e);
        } catch (IOException ioe) {
            throw new EncryptionException(IO_ERR_MSG, ioe);
        } catch (IllegalArgumentException e) {
            throw new EncryptionException(STD_ERR_MSG, e);
        } catch (CMSException e) {
            throw new EncryptionException(STD_ERR_MSG, e);
        }
        encryptedMimeData = (encryptedMimeData == null) ? new byte[] {} : encryptedMimeData;
        return encryptedMimeData;
    }

    /**
     * Decrypts the given byte data with the given receiver certificate and the
     * private key
     * 
     * @param encryptedByteData
     *            an array of byte data to decrypt
     * @param x509CertificateFile
     *            X.509 certificate that was used to encrypt the data. The file
     *            is expected to be in DER or PEM format
     * @param privateKeyPemFile
     *            .pem file that contains the private key used for decryption.
     *            This key must fit to the public key contained in the public
     *            certificate
     * @param privateKeyPassword
     *            password to encrypt private key
     * @return an array of bytes representing the unencrypted byte data.
     * @throws IOException
     *             <ul>
     *             <li>if any of the given files does not exist</li>
     *             <li>if any of the given files cannot be read</li>
     *             </ul>
     * @throws CertificateNotYetValidException
     *             if the certificate is not yet valid
     * @throws CertificateExpiredException
     *             if the certificate is not valid anymore
     * @throws CertificateException
     *             <ul>
     *             <li>if the given certificate file does not contain a
     *             certificate</li>
     *             <li>if the certificate contained in the given file is not a
     *             X.509 certificate</li>
     *             </ul>
     * @throws EncryptionException
     *             if a problem occured during the encryption process
     */
    public static byte[] decrypt(byte[] encryptedByteData, File x509CertificateFile,
            File privateKeyPemFile, final String privateKeyPassword)
            throws IOException, CertificateException, EncryptionException {

        byte[] decryptedByteData = new byte[] {};

        // Get public key certificate
        X509Certificate x509Certificate = CertificateUtils
                .loadX509CertificateFromFile(x509CertificateFile);

        // The recipient's private key
        FileReader fileReader = new FileReader(privateKeyPemFile);
        PasswordFinder passwordFinder = new PasswordFinder() {
            @Override
            public char[] getPassword() {
                return (privateKeyPassword != null) ? privateKeyPassword.toCharArray() : null;
            }
        };
        PEMReader pemReader = null;
        if (passwordFinder.getPassword() != null && passwordFinder.getPassword().length > 0) {
            pemReader = new PEMReader(fileReader, passwordFinder);
        } else {
            pemReader = new PEMReader(fileReader);
        }
        JCERSAPrivateCrtKey privateCertKey = (JCERSAPrivateCrtKey) pemReader.readObject();

        try {
            CMSEnvelopedDataParser cedParser = new CMSEnvelopedDataParser(encryptedByteData);

            // look for our recipient identifier
            RecipientId recipientId = new JceKeyAgreeRecipientId(x509Certificate);

            RecipientInformationStore recipients = cedParser.getRecipientInfos();
            RecipientInformation recipientInfo = null;
            for (Object recipient : recipients.getRecipients()) {
                try {
                    recipientInfo = (RecipientInformation) recipient;
                } catch (Exception e) {
                    log.error("Error determing recipient", e);
                }
            }

            if (recipientInfo != null) {
                JceKeyTransRecipient rec = new JceKeyTransEnvelopedRecipient(privateCertKey);
                rec.setProvider(BouncyCastleProvider.PROVIDER_NAME);
                rec.setContentProvider(BouncyCastleProvider.PROVIDER_NAME);
                decryptedByteData = recipientInfo.getContent(rec);
            }
        } catch (CMSException e) {
            throw new EncryptionException(IO_ERR_MSG, e);
        }

        decryptedByteData = Base64.decode(decryptedByteData);
        return decryptedByteData;
    }

}