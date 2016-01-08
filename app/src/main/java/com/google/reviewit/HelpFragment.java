// Copyright (C) 2016 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.reviewit;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.google.reviewit.util.WidgetUtil;

/** Fragment to show the help text for the application. */
public class HelpFragment extends BaseFragment {

    @Override
    protected @LayoutRes int getLayout() {
      return R.layout.content_help;
    }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    WidgetUtil.setText(v(R.id.license_apache2_0), "\n" +
        "Apache License\n" +
        "Version 2.0, January 2004\n" +
        "http://www.apache.org/licenses/\n" +
        "\n" +
        "   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
        "\n" +
        "   1. Definitions.\n" +
        "\n" +
        "      \"License\" shall mean the terms and conditions for use, reproduction,\n" +
        "      and distribution as defined by Sections 1 through 9 of this document.\n" +
        "\n" +
        "      \"Licensor\" shall mean the copyright owner or entity authorized by\n" +
        "      the copyright owner that is granting the License.\n" +
        "\n" +
        "      \"Legal Entity\" shall mean the union of the acting entity and all\n" +
        "      other entities that control, are controlled by, or are under common\n" +
        "      control with that entity. For the purposes of this definition,\n" +
        "      \"control\" means (i) the power, direct or indirect, to cause the\n" +
        "      direction or management of such entity, whether by contract or\n" +
        "      otherwise, or (ii) ownership of fifty percent (50%) or more of the\n" +
        "      outstanding shares, or (iii) beneficial ownership of such entity.\n" +
        "\n" +
        "      \"You\" (or \"Your\") shall mean an individual or Legal Entity\n" +
        "      exercising permissions granted by this License.\n" +
        "\n" +
        "      \"Source\" form shall mean the preferred form for making modifications,\n" +
        "      including but not limited to software source code, documentation\n" +
        "      source, and configuration files.\n" +
        "\n" +
        "      \"Object\" form shall mean any form resulting from mechanical\n" +
        "      transformation or translation of a Source form, including but\n" +
        "      not limited to compiled object code, generated documentation,\n" +
        "      and conversions to other media types.\n" +
        "\n" +
        "      \"Work\" shall mean the work of authorship, whether in Source or\n" +
        "      Object form, made available under the License, as indicated by a\n" +
        "      copyright notice that is included in or attached to the work\n" +
        "      (an example is provided in the Appendix below).\n" +
        "\n" +
        "      \"Derivative Works\" shall mean any work, whether in Source or Object\n" +
        "      form, that is based on (or derived from) the Work and for which the\n" +
        "      editorial revisions, annotations, elaborations, or other modifications\n" +
        "      represent, as a whole, an original work of authorship. For the purposes\n" +
        "      of this License, Derivative Works shall not include works that remain\n" +
        "      separable from, or merely link (or bind by name) to the interfaces of,\n" +
        "      the Work and Derivative Works thereof.\n" +
        "\n" +
        "      \"Contribution\" shall mean any work of authorship, including\n" +
        "      the original version of the Work and any modifications or additions\n" +
        "      to that Work or Derivative Works thereof, that is intentionally\n" +
        "      submitted to Licensor for inclusion in the Work by the copyright owner\n" +
        "      or by an individual or Legal Entity authorized to submit on behalf of\n" +
        "      the copyright owner. For the purposes of this definition, \"submitted\"\n" +
        "      means any form of electronic, verbal, or written communication sent\n" +
        "      to the Licensor or its representatives, including but not limited to\n" +
        "      communication on electronic mailing lists, source code control systems,\n" +
        "      and issue tracking systems that are managed by, or on behalf of, the\n" +
        "      Licensor for the purpose of discussing and improving the Work, but\n" +
        "      excluding communication that is conspicuously marked or otherwise\n" +
        "      designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
        "\n" +
        "      \"Contributor\" shall mean Licensor and any individual or Legal Entity\n" +
        "      on behalf of whom a Contribution has been received by Licensor and\n" +
        "      subsequently incorporated within the Work.\n" +
        "\n" +
        "   2. Grant of Copyright License. Subject to the terms and conditions of\n" +
        "      this License, each Contributor hereby grants to You a perpetual,\n" +
        "      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
        "      copyright license to reproduce, prepare Derivative Works of,\n" +
        "      publicly display, publicly perform, sublicense, and distribute the\n" +
        "      Work and such Derivative Works in Source or Object form.\n" +
        "\n" +
        "   3. Grant of Patent License. Subject to the terms and conditions of\n" +
        "      this License, each Contributor hereby grants to You a perpetual,\n" +
        "      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
        "      (except as stated in this section) patent license to make, have made,\n" +
        "      use, offer to sell, sell, import, and otherwise transfer the Work,\n" +
        "      where such license applies only to those patent claims licensable\n" +
        "      by such Contributor that are necessarily infringed by their\n" +
        "      Contribution(s) alone or by combination of their Contribution(s)\n" +
        "      with the Work to which such Contribution(s) was submitted. If You\n" +
        "      institute patent litigation against any entity (including a\n" +
        "      cross-claim or counterclaim in a lawsuit) alleging that the Work\n" +
        "      or a Contribution incorporated within the Work constitutes direct\n" +
        "      or contributory patent infringement, then any patent licenses\n" +
        "      granted to You under this License for that Work shall terminate\n" +
        "      as of the date such litigation is filed.\n" +
        "\n" +
        "   4. Redistribution. You may reproduce and distribute copies of the\n" +
        "      Work or Derivative Works thereof in any medium, with or without\n" +
        "      modifications, and in Source or Object form, provided that You\n" +
        "      meet the following conditions:\n" +
        "\n" +
        "      (a) You must give any other recipients of the Work or\n" +
        "          Derivative Works a copy of this License; and\n" +
        "\n" +
        "      (b) You must cause any modified files to carry prominent notices\n" +
        "          stating that You changed the files; and\n" +
        "\n" +
        "      (c) You must retain, in the Source form of any Derivative Works\n" +
        "          that You distribute, all copyright, patent, trademark, and\n" +
        "          attribution notices from the Source form of the Work,\n" +
        "          excluding those notices that do not pertain to any part of\n" +
        "          the Derivative Works; and\n" +
        "\n" +
        "      (d) If the Work includes a \"NOTICE\" text file as part of its\n" +
        "          distribution, then any Derivative Works that You distribute must\n" +
        "          include a readable copy of the attribution notices contained\n" +
        "          within such NOTICE file, excluding those notices that do not\n" +
        "          pertain to any part of the Derivative Works, in at least one\n" +
        "          of the following places: within a NOTICE text file distributed\n" +
        "          as part of the Derivative Works; within the Source form or\n" +
        "          documentation, if provided along with the Derivative Works; or,\n" +
        "          within a display generated by the Derivative Works, if and\n" +
        "          wherever such third-party notices normally appear. The contents\n" +
        "          of the NOTICE file are for informational purposes only and\n" +
        "          do not modify the License. You may add Your own attribution\n" +
        "          notices within Derivative Works that You distribute, alongside\n" +
        "          or as an addendum to the NOTICE text from the Work, provided\n" +
        "          that such additional attribution notices cannot be construed\n" +
        "          as modifying the License.\n" +
        "\n" +
        "      You may add Your own copyright statement to Your modifications and\n" +
        "      may provide additional or different license terms and conditions\n" +
        "      for use, reproduction, or distribution of Your modifications, or\n" +
        "      for any such Derivative Works as a whole, provided Your use,\n" +
        "      reproduction, and distribution of the Work otherwise complies with\n" +
        "      the conditions stated in this License.\n" +
        "\n" +
        "   5. Submission of Contributions. Unless You explicitly state otherwise,\n" +
        "      any Contribution intentionally submitted for inclusion in the Work\n" +
        "      by You to the Licensor shall be under the terms and conditions of\n" +
        "      this License, without any additional terms or conditions.\n" +
        "      Notwithstanding the above, nothing herein shall supersede or modify\n" +
        "      the terms of any separate license agreement you may have executed\n" +
        "      with Licensor regarding such Contributions.\n" +
        "\n" +
        "   6. Trademarks. This License does not grant permission to use the trade\n" +
        "      names, trademarks, service marks, or product names of the Licensor,\n" +
        "      except as required for reasonable and customary use in describing the\n" +
        "      origin of the Work and reproducing the content of the NOTICE file.\n" +
        "\n" +
        "   7. Disclaimer of Warranty. Unless required by applicable law or\n" +
        "      agreed to in writing, Licensor provides the Work (and each\n" +
        "      Contributor provides its Contributions) on an \"AS IS\" BASIS,\n" +
        "      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n" +
        "      implied, including, without limitation, any warranties or conditions\n" +
        "      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\n" +
        "      PARTICULAR PURPOSE. You are solely responsible for determining the\n" +
        "      appropriateness of using or redistributing the Work and assume any\n" +
        "      risks associated with Your exercise of permissions under this License.\n" +
        "\n" +
        "   8. Limitation of Liability. In no event and under no legal theory,\n" +
        "      whether in tort (including negligence), contract, or otherwise,\n" +
        "      unless required by applicable law (such as deliberate and grossly\n" +
        "      negligent acts) or agreed to in writing, shall any Contributor be\n" +
        "      liable to You for damages, including any direct, indirect, special,\n" +
        "      incidental, or consequential damages of any character arising as a\n" +
        "      result of this License or out of the use or inability to use the\n" +
        "      Work (including but not limited to damages for loss of goodwill,\n" +
        "      work stoppage, computer failure or malfunction, or any and all\n" +
        "      other commercial damages or losses), even if such Contributor\n" +
        "      has been advised of the possibility of such damages.\n" +
        "\n" +
        "   9. Accepting Warranty or Additional Liability. While redistributing\n" +
        "      the Work or Derivative Works thereof, You may choose to offer,\n" +
        "      and charge a fee for, acceptance of support, warranty, indemnity,\n" +
        "      or other liability obligations and/or rights consistent with this\n" +
        "      License. However, in accepting such obligations, You may act only\n" +
        "      on Your own behalf and on Your sole responsibility, not on behalf\n" +
        "      of any other Contributor, and only if You agree to indemnify,\n" +
        "      defend, and hold each Contributor harmless for any liability\n" +
        "      incurred by, or claims asserted against, such Contributor by reason\n" +
        "      of your accepting any such warranty or additional liability.\n" +
        "\n" +
        "   END OF TERMS AND CONDITIONS\n" +
        "\n" +
        "   APPENDIX: How to apply the Apache License to your work.\n" +
        "\n" +
        "      To apply the Apache License to your work, attach the following\n" +
        "      boilerplate notice, with the fields enclosed by brackets \"[]\"\n" +
        "      replaced with your own identifying information. (Don't include\n" +
        "      the brackets!)  The text should be enclosed in the appropriate\n" +
        "      comment syntax for the file format. We also recommend that a\n" +
        "      file or class name and description of purpose be included on the\n" +
        "      same \"printed page\" as the copyright notice for easier\n" +
        "      identification within third-party archives.\n" +
        "\n" +
        "   Copyright [yyyy] [name of copyright owner]\n" +
        "\n" +
        "   Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
        "   you may not use this file except in compliance with the License.\n" +
        "   You may obtain a copy of the License at\n" +
        "\n" +
        "       http://www.apache.org/licenses/LICENSE-2.0\n" +
        "\n" +
        "   Unless required by applicable law or agreed to in writing, software\n" +
        "   distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
        "   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
        "   See the License for the specific language governing permissions and\n" +
        "   limitations under the License.\n");
    WidgetUtil.setText(v(R.id.license_cc_by_3_0), "\n" +
        "THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS\n" +
        "CREATIVE COMMONS PUBLIC LICENSE (\"CCPL\" OR \"LICENSE\").  THE WORK IS\n" +
        "PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  ANY USE OF THE\n" +
        "WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR COPYRIGHT LAW IS\n" +
        "PROHIBITED.\n" +
        "\n" +
        "BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND\n" +
        "AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE.  TO THE EXTENT THIS\n" +
        "LICENSE MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU\n" +
        "THE RIGHTS CONTAINED HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH\n" +
        "TERMS AND CONDITIONS.\n" +
        "\n" +
        "1.  Definitions\n" +
        "\n" +
        "  a.  \"Adaptation\" means a work based upon the Work, or upon the Work\n" +
        "      and other pre-existing works, such as a translation, adaptation,\n" +
        "      derivative work, arrangement of music or other alterations of a\n" +
        "      literary or artistic work, or phonogram or performance and\n" +
        "      includes cinematographic adaptations or any other form in which\n" +
        "      the Work may be recast, transformed, or adapted including in any\n" +
        "      form recognizably derived from the original, except that a work\n" +
        "      that constitutes a Collection will not be considered an\n" +
        "      Adaptation for the purpose of this License.  For the avoidance\n" +
        "      of doubt, where the Work is a musical work, performance or\n" +
        "      phonogram, the synchronization of the Work in timed-relation\n" +
        "      with a moving image (\"synching\") will be considered an\n" +
        "      Adaptation for the purpose of this License.\n" +
        "\n" +
        "  b.  \"Collection\" means a collection of literary or artistic works,\n" +
        "      such as encyclopedias and anthologies, or performances,\n" +
        "      phonograms or broadcasts, or other works or subject matter other\n" +
        "      than works listed in Section 1(f) below, which, by reason of the\n" +
        "      selection and arrangement of their contents, constitute\n" +
        "      intellectual creations, in which the Work is included in its\n" +
        "      entirety in unmodified form along with one or more other\n" +
        "      contributions, each constituting separate and independent works\n" +
        "      in themselves, which together are assembled into a collective\n" +
        "      whole.  A work that constitutes a Collection will not be\n" +
        "      considered an Adaptation (as defined above) for the purposes of\n" +
        "      this License.\n" +
        "\n" +
        "  c.  \"Distribute\" means to make available to the public the original\n" +
        "      and copies of the Work or Adaptation, as appropriate, through\n" +
        "      sale or other transfer of ownership.\n" +
        "\n" +
        "  d.  \"Licensor\" means the individual, individuals, entity or entities\n" +
        "      that offer(s) the Work under the terms of this License.\n" +
        "\n" +
        "  e.  \"Original Author\" means, in the case of a literary or artistic\n" +
        "      work, the individual, individuals, entity or entities who\n" +
        "      created the Work or if no individual or entity can be\n" +
        "      identified, the publisher; and in addition (i) in the case of a\n" +
        "      performance the actors, singers, musicians, dancers, and other\n" +
        "      persons who act, sing, deliver, declaim, play in, interpret or\n" +
        "      otherwise perform literary or artistic works or expressions of\n" +
        "      folklore; (ii) in the case of a phonogram the producer being the\n" +
        "      person or legal entity who first fixes the sounds of a\n" +
        "      performance or other sounds; and, (iii) in the case of\n" +
        "      broadcasts, the organization that transmits the broadcast.\n" +
        "\n" +
        "  f.  \"Work\" means the literary and/or artistic work offered under the\n" +
        "      terms of this License including without limitation any\n" +
        "      production in the literary, scientific and artistic domain,\n" +
        "      whatever may be the mode or form of its expression including\n" +
        "      digital form, such as a book, pamphlet and other writing; a\n" +
        "      lecture, address, sermon or other work of the same nature; a\n" +
        "      dramatic or dramatico-musical work; a choreographic work or\n" +
        "      entertainment in dumb show; a musical composition with or\n" +
        "      without words; a cinematographic work to which are assimilated\n" +
        "      works expressed by a process analogous to cinematography; a work\n" +
        "      of drawing, painting, architecture, sculpture, engraving or\n" +
        "      lithography; a photographic work to which are assimilated works\n" +
        "      expressed by a process analogous to photography; a work of\n" +
        "      applied art; an illustration, map, plan, sketch or\n" +
        "      three-dimensional work relative to geography, topography,\n" +
        "      architecture or science; a performance; a broadcast; a\n" +
        "      phonogram; a compilation of data to the extent it is protected\n" +
        "      as a copyrightable work; or a work performed by a variety or\n" +
        "      circus performer to the extent it is not otherwise considered a\n" +
        "      literary or artistic work.\n" +
        "\n" +
        "  g.  \"You\" means an individual or entity exercising rights under this\n" +
        "      License who has not previously violated the terms of this\n" +
        "      License with respect to the Work, or who has received express\n" +
        "      permission from the Licensor to exercise rights under this\n" +
        "      License despite a previous violation.\n" +
        "\n" +
        "  h.  \"Publicly Perform\" means to perform public recitations of the\n" +
        "      Work and to communicate to the public those public recitations,\n" +
        "      by any means or process, including by wire or wireless means or\n" +
        "      public digital performances; to make available to the public\n" +
        "      Works in such a way that members of the public may access these\n" +
        "      Works from a place and at a place individually chosen by them;\n" +
        "      to perform the Work to the public by any means or process and\n" +
        "      the communication to the public of the performances of the Work,\n" +
        "      including by public digital performance; to broadcast and\n" +
        "      rebroadcast the Work by any means including signs, sounds or\n" +
        "      images.\n" +
        "\n" +
        "  i.  \"Reproduce\" means to make copies of the Work by any means\n" +
        "      including without limitation by sound or visual recordings and\n" +
        "      the right of fixation and reproducing fixations of the Work,\n" +
        "      including storage of a protected performance or phonogram in\n" +
        "      digital form or other electronic medium.\n" +
        "\n" +
        "2.  Fair Dealing Rights.  Nothing in this License is intended to\n" +
        "    reduce, limit, or restrict any uses free from copyright or rights\n" +
        "    arising from limitations or exceptions that are provided for in\n" +
        "    connection with the copyright protection under copyright law or\n" +
        "    other applicable laws.\n" +
        "\n" +
        "3.  License Grant.  Subject to the terms and conditions of this\n" +
        "    License, Licensor hereby grants You a worldwide, royalty-free,\n" +
        "    non-exclusive, perpetual (for the duration of the applicable\n" +
        "    copyright) license to exercise the rights in the Work as stated\n" +
        "    below:\n" +
        "\n" +
        "  a.  to Reproduce the Work, to incorporate the Work into one or more\n" +
        "      Collections, and to Reproduce the Work as incorporated in the\n" +
        "      Collections;\n" +
        "\n" +
        "  b.  to create and Reproduce Adaptations provided that any such\n" +
        "      Adaptation, including any translation in any medium, takes\n" +
        "      reasonable steps to clearly label, demarcate or otherwise\n" +
        "      identify that changes were made to the original Work.  For\n" +
        "      example, a translation could be marked \"The original work was\n" +
        "      translated from English to Spanish,\" or a modification could\n" +
        "      indicate \"The original work has been modified.\";\n" +
        "\n" +
        "  c.  to Distribute and Publicly Perform the Work including as\n" +
        "      incorporated in Collections; and,\n" +
        "\n" +
        "  d.  to Distribute and Publicly Perform Adaptations.\n" +
        "\n" +
        "  e.  For the avoidance of doubt:\n" +
        "\n" +
        "    i.   Non-waivable Compulsory License Schemes.  In those\n" +
        "\t     jurisdictions in which the right to collect royalties\n" +
        "\t     through any statutory or compulsory licensing scheme\n" +
        "\t     cannot be waived, the Licensor reserves the exclusive\n" +
        "\t     right to collect such royalties for any exercise by You\n" +
        "\t     of the rights granted under this License;\n" +
        "\n" +
        "    ii.  Waivable Compulsory License Schemes.  In those jurisdictions\n" +
        "\t     in which the right to collect royalties through any\n" +
        "\t     statutory or compulsory licensing scheme can be waived,\n" +
        "\t     the Licensor waives the exclusive right to collect such\n" +
        "\t     royalties for any exercise by You of the rights granted\n" +
        "\t     under this License; and,\n" +
        "\n" +
        "    iii. Voluntary License Schemes.  The Licensor waives the right to\n" +
        "\t     collect royalties, whether individually or, in the event\n" +
        "\t     that the Licensor is a member of a collecting society\n" +
        "\t     that administers voluntary licensing schemes, via that\n" +
        "\t     society, from any exercise by You of the rights granted\n" +
        "\t     under this License.\n" +
        "\n" +
        "The above rights may be exercised in all media and formats whether now\n" +
        "known or hereafter devised.  The above rights include the right to\n" +
        "make such modifications as are technically necessary to exercise the\n" +
        "rights in other media and formats.  Subject to Section 8(f), all\n" +
        "rights not expressly granted by Licensor are hereby reserved.\n" +
        "\n" +
        "4.  Restrictions.  The license granted in Section 3 above is expressly\n" +
        "    made subject to and limited by the following restrictions:\n" +
        "\n" +
        "  a.  You may Distribute or Publicly Perform the Work only under the\n" +
        "      terms of this License.  You must include a copy of, or the\n" +
        "      Uniform Resource Identifier (URI) for, this License with every\n" +
        "      copy of the Work You Distribute or Publicly Perform.  You may\n" +
        "      not offer or impose any terms on the Work that restrict the\n" +
        "      terms of this License or the ability of the recipient of the\n" +
        "      Work to exercise the rights granted to that recipient under the\n" +
        "      terms of the License.  You may not sublicense the Work.  You\n" +
        "      must keep intact all notices that refer to this License and to\n" +
        "      the disclaimer of warranties with every copy of the Work You\n" +
        "      Distribute or Publicly Perform.  When You Distribute or Publicly\n" +
        "      Perform the Work, You may not impose any effective technological\n" +
        "      measures on the Work that restrict the ability of a recipient of\n" +
        "      the Work from You to exercise the rights granted to that\n" +
        "      recipient under the terms of the License.  This Section 4(a)\n" +
        "      applies to the Work as incorporated in a Collection, but this\n" +
        "      does not require the Collection apart from the Work itself to be\n" +
        "      made subject to the terms of this License.  If You create a\n" +
        "      Collection, upon notice from any Licensor You must, to the\n" +
        "      extent practicable, remove from the Collection any credit as\n" +
        "      required by Section 4(b), as requested.  If You create an\n" +
        "      Adaptation, upon notice from any Licensor You must, to the\n" +
        "      extent practicable, remove from the Adaptation any credit as\n" +
        "      required by Section 4(b), as requested.\n" +
        "\n" +
        "  b.  If You Distribute, or Publicly Perform the Work or any\n" +
        "      Adaptations or Collections, You must, unless a request has been\n" +
        "      made pursuant to Section 4(a), keep intact all copyright notices\n" +
        "      for the Work and provide, reasonable to the medium or means You\n" +
        "      are utilizing: (i) the name of the Original Author (or\n" +
        "      pseudonym, if applicable) if supplied, and/or if the Original\n" +
        "      Author and/or Licensor designate another party or parties (e.g.,\n" +
        "      a sponsor institute, publishing entity, journal) for attribution\n" +
        "      (\"Attribution Parties\") in Licensor's copyright notice, terms of\n" +
        "      service or by other reasonable means, the name of such party or\n" +
        "      parties; (ii) the title of the Work if supplied; (iii) to the\n" +
        "      extent reasonably practicable, the URI, if any, that Licensor\n" +
        "      specifies to be associated with the Work, unless such URI does\n" +
        "      not refer to the copyright notice or licensing information for\n" +
        "      the Work; and (iv) , consistent with Section 3(b), in the case\n" +
        "      of an Adaptation, a credit identifying the use of the Work in\n" +
        "      the Adaptation (e.g., \"French translation of the Work by\n" +
        "      Original Author,\" or \"Screenplay based on original Work by\n" +
        "      Original Author\").  The credit required by this Section 4 (b)\n" +
        "      may be implemented in any reasonable manner; provided, however,\n" +
        "      that in the case of a Adaptation or Collection, at a minimum\n" +
        "      such credit will appear, if a credit for all contributing\n" +
        "      authors of the Adaptation or Collection appears, then as part of\n" +
        "      these credits and in a manner at least as prominent as the\n" +
        "      credits for the other contributing authors.  For the avoidance\n" +
        "      of doubt, You may only use the credit required by this Section\n" +
        "      for the purpose of attribution in the manner set out above and,\n" +
        "      by exercising Your rights under this License, You may not\n" +
        "      implicitly or explicitly assert or imply any connection with,\n" +
        "      sponsorship or endorsement by the Original Author, Licensor\n" +
        "      and/or Attribution Parties, as appropriate, of You or Your use\n" +
        "      of the Work, without the separate, express prior written\n" +
        "      permission of the Original Author, Licensor and/or Attribution\n" +
        "      Parties.\n" +
        "\n" +
        "  c.  Except as otherwise agreed in writing by the Licensor or as may\n" +
        "      be otherwise permitted by applicable law, if You Reproduce,\n" +
        "      Distribute or Publicly Perform the Work either by itself or as\n" +
        "      part of any Adaptations or Collections, You must not distort,\n" +
        "      mutilate, modify or take other derogatory action in relation to\n" +
        "      the Work which would be prejudicial to the Original Author's\n" +
        "      honor or reputation.  Licensor agrees that in those\n" +
        "      jurisdictions (e.g.  Japan), in which any exercise of the right\n" +
        "      granted in Section 3(b) of this License (the right to make\n" +
        "      Adaptations) would be deemed to be a distortion, mutilation,\n" +
        "      modification or other derogatory action prejudicial to the\n" +
        "      Original Author's honor and reputation, the Licensor will waive\n" +
        "      or not assert, as appropriate, this Section, to the fullest\n" +
        "      extent permitted by the applicable national law, to enable You\n" +
        "      to reasonably exercise Your right under Section 3(b) of this\n" +
        "      License (right to make Adaptations) but not otherwise.\n" +
        "\n" +
        "5.  Representations, Warranties and Disclaimer\n" +
        "\n" +
        "UNLESS OTHERWISE MUTUALLY AGREED TO BY THE PARTIES IN WRITING,\n" +
        "LICENSOR OFFERS THE WORK AS-IS AND MAKES NO REPRESENTATIONS OR\n" +
        "WARRANTIES OF ANY KIND CONCERNING THE WORK, EXPRESS, IMPLIED,\n" +
        "STATUTORY OR OTHERWISE, INCLUDING, WITHOUT LIMITATION, WARRANTIES OF\n" +
        "TITLE, MERCHANTIBILITY, FITNESS FOR A PARTICULAR PURPOSE,\n" +
        "NONINFRINGEMENT, OR THE ABSENCE OF LATENT OR OTHER DEFECTS, ACCURACY,\n" +
        "OR THE PRESENCE OF ABSENCE OF ERRORS, WHETHER OR NOT DISCOVERABLE.\n" +
        "SOME JURISDICTIONS DO NOT ALLOW THE EXCLUSION OF IMPLIED WARRANTIES,\n" +
        "SO SUCH EXCLUSION MAY NOT APPLY TO YOU.\n" +
        "\n" +
        "6.  Limitation on Liability.  EXCEPT TO THE EXTENT REQUIRED BY\n" +
        "    APPLICABLE LAW, IN NO EVENT WILL LICENSOR BE LIABLE TO YOU ON ANY\n" +
        "    LEGAL THEORY FOR ANY SPECIAL, INCIDENTAL, CONSEQUENTIAL, PUNITIVE\n" +
        "    OR EXEMPLARY DAMAGES ARISING OUT OF THIS LICENSE OR THE USE OF THE\n" +
        "    WORK, EVEN IF LICENSOR HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH\n" +
        "    DAMAGES.\n" +
        "\n" +
        "7.  Termination\n" +
        "\n" +
        "  a.  This License and the rights granted hereunder will terminate\n" +
        "      automatically upon any breach by You of the terms of this\n" +
        "      License.  Individuals or entities who have received Adaptations\n" +
        "      or Collections from You under this License, however, will not\n" +
        "      have their licenses terminated provided such individuals or\n" +
        "      entities remain in full compliance with those licenses.\n" +
        "      Sections 1, 2, 5, 6, 7, and 8 will survive any termination of\n" +
        "      this License.\n" +
        "\n" +
        "  b.  Subject to the above terms and conditions, the license granted\n" +
        "      here is perpetual (for the duration of the applicable copyright\n" +
        "      in the Work).  Notwithstanding the above, Licensor reserves the\n" +
        "      right to release the Work under different license terms or to\n" +
        "      stop distributing the Work at any time; provided, however that\n" +
        "      any such election will not serve to withdraw this License (or\n" +
        "      any other license that has been, or is required to be, granted\n" +
        "      under the terms of this License), and this License will continue\n" +
        "      in full force and effect unless terminated as stated above.\n" +
        "\n" +
        "8. Miscellaneous\n" +
        "\n" +
        "  a.  Each time You Distribute or Publicly Perform the Work or a\n" +
        "      Collection, the Licensor offers to the recipient a license to\n" +
        "      the Work on the same terms and conditions as the license granted\n" +
        "      to You under this License.\n" +
        "\n" +
        "  b.  Each time You Distribute or Publicly Perform an Adaptation,\n" +
        "      Licensor offers to the recipient a license to the original Work\n" +
        "      on the same terms and conditions as the license granted to You\n" +
        "      under this License.\n" +
        "\n" +
        "  c.  If any provision of this License is invalid or unenforceable\n" +
        "      under applicable law, it shall not affect the validity or\n" +
        "      enforceability of the remainder of the terms of this License,\n" +
        "      and without further action by the parties to this agreement,\n" +
        "      such provision shall be reformed to the minimum extent necessary\n" +
        "      to make such provision valid and enforceable.\n" +
        "\n" +
        "  d.  No term or provision of this License shall be deemed waived and\n" +
        "      no breach consented to unless such waiver or consent shall be in\n" +
        "      writing and signed by the party to be charged with such waiver\n" +
        "      or consent.\n" +
        "\n" +
        "  e.  This License constitutes the entire agreement between the\n" +
        "      parties with respect to the Work licensed here.  There are no\n" +
        "      understandings, agreements or representations with respect to\n" +
        "      the Work not specified here.  Licensor shall not be bound by any\n" +
        "      additional provisions that may appear in any communication from\n" +
        "      You.  This License may not be modified without the mutual\n" +
        "      written agreement of the Licensor and You.\n" +
        "\n" +
        "  f.  The rights granted under, and the subject matter referenced, in\n" +
        "      this License were drafted utilizing the terminology of the Berne\n" +
        "      Convention for the Protection of Literary and Artistic Works (as\n" +
        "      amended on September 28, 1979), the Rome Convention of 1961, the\n" +
        "      WIPO Copyright Treaty of 1996, the WIPO Performances and\n" +
        "      Phonograms Treaty of 1996 and the Universal Copyright Convention\n" +
        "      (as revised on July 24, 1971).  These rights and subject matter\n" +
        "      take effect in the relevant jurisdiction in which the License\n" +
        "      terms are sought to be enforced according to the corresponding\n" +
        "      provisions of the implementation of those treaty provisions in\n" +
        "      the applicable national law.  If the standard suite of rights\n" +
        "      granted under applicable copyright law includes additional\n" +
        "      rights not granted under this License, such additional rights\n" +
        "      are deemed to be included in the License; this License is not\n" +
        "      intended to restrict the license of any rights under applicable\n" +
        "      law.\n");
    WidgetUtil.setText(v(R.id.license_cc_by_4_0), "\n" +
        "Attribution 4.0 International\n" +
        "\n" +
        "=======================================================================\n" +
        "\n" +
        "Creative Commons Corporation (\"Creative Commons\") is not a law firm and\n" +
        "does not provide legal services or legal advice. Distribution of\n" +
        "Creative Commons public licenses does not create a lawyer-client or\n" +
        "other relationship. Creative Commons makes its licenses and related\n" +
        "information available on an \"as-is\" basis. Creative Commons gives no\n" +
        "warranties regarding its licenses, any material licensed under their\n" +
        "terms and conditions, or any related information. Creative Commons\n" +
        "disclaims all liability for damages resulting from their use to the\n" +
        "fullest extent possible.\n" +
        "\n" +
        "Using Creative Commons Public Licenses\n" +
        "\n" +
        "Creative Commons public licenses provide a standard set of terms and\n" +
        "conditions that creators and other rights holders may use to share\n" +
        "original works of authorship and other material subject to copyright\n" +
        "and certain other rights specified in the public license below. The\n" +
        "following considerations are for informational purposes only, are not\n" +
        "exhaustive, and do not form part of our licenses.\n" +
        "\n" +
        "     Considerations for licensors: Our public licenses are\n" +
        "     intended for use by those authorized to give the public\n" +
        "     permission to use material in ways otherwise restricted by\n" +
        "     copyright and certain other rights. Our licenses are\n" +
        "     irrevocable. Licensors should read and understand the terms\n" +
        "     and conditions of the license they choose before applying it.\n" +
        "     Licensors should also secure all rights necessary before\n" +
        "     applying our licenses so that the public can reuse the\n" +
        "     material as expected. Licensors should clearly mark any\n" +
        "     material not subject to the license. This includes other CC-\n" +
        "     licensed material, or material used under an exception or\n" +
        "     limitation to copyright. More considerations for licensors:\n" +
        "\twiki.creativecommons.org/Considerations_for_licensors\n" +
        "\n" +
        "     Considerations for the public: By using one of our public\n" +
        "     licenses, a licensor grants the public permission to use the\n" +
        "     licensed material under specified terms and conditions. If\n" +
        "     the licensor's permission is not necessary for any reason--for\n" +
        "     example, because of any applicable exception or limitation to\n" +
        "     copyright--then that use is not regulated by the license. Our\n" +
        "     licenses grant only permissions under copyright and certain\n" +
        "     other rights that a licensor has authority to grant. Use of\n" +
        "     the licensed material may still be restricted for other\n" +
        "     reasons, including because others have copyright or other\n" +
        "     rights in the material. A licensor may make special requests,\n" +
        "     such as asking that all changes be marked or described.\n" +
        "     Although not required by our licenses, you are encouraged to\n" +
        "     respect those requests where reasonable. More_considerations\n" +
        "     for the public: \n" +
        "\twiki.creativecommons.org/Considerations_for_licensees\n" +
        "\n" +
        "=======================================================================\n" +
        "\n" +
        "Creative Commons Attribution 4.0 International Public License\n" +
        "\n" +
        "By exercising the Licensed Rights (defined below), You accept and agree\n" +
        "to be bound by the terms and conditions of this Creative Commons\n" +
        "Attribution 4.0 International Public License (\"Public License\"). To the\n" +
        "extent this Public License may be interpreted as a contract, You are\n" +
        "granted the Licensed Rights in consideration of Your acceptance of\n" +
        "these terms and conditions, and the Licensor grants You such rights in\n" +
        "consideration of benefits the Licensor receives from making the\n" +
        "Licensed Material available under these terms and conditions.\n" +
        "\n" +
        "\n" +
        "Section 1 -- Definitions.\n" +
        "\n" +
        "  a. Adapted Material means material subject to Copyright and Similar\n" +
        "     Rights that is derived from or based upon the Licensed Material\n" +
        "     and in which the Licensed Material is translated, altered,\n" +
        "     arranged, transformed, or otherwise modified in a manner requiring\n" +
        "     permission under the Copyright and Similar Rights held by the\n" +
        "     Licensor. For purposes of this Public License, where the Licensed\n" +
        "     Material is a musical work, performance, or sound recording,\n" +
        "     Adapted Material is always produced where the Licensed Material is\n" +
        "     synched in timed relation with a moving image.\n" +
        "\n" +
        "  b. Adapter's License means the license You apply to Your Copyright\n" +
        "     and Similar Rights in Your contributions to Adapted Material in\n" +
        "     accordance with the terms and conditions of this Public License.\n" +
        "\n" +
        "  c. Copyright and Similar Rights means copyright and/or similar rights\n" +
        "     closely related to copyright including, without limitation,\n" +
        "     performance, broadcast, sound recording, and Sui Generis Database\n" +
        "     Rights, without regard to how the rights are labeled or\n" +
        "     categorized. For purposes of this Public License, the rights\n" +
        "     specified in Section 2(b)(1)-(2) are not Copyright and Similar\n" +
        "     Rights.\n" +
        "\n" +
        "  d. Effective Technological Measures means those measures that, in the\n" +
        "     absence of proper authority, may not be circumvented under laws\n" +
        "     fulfilling obligations under Article 11 of the WIPO Copyright\n" +
        "     Treaty adopted on December 20, 1996, and/or similar international\n" +
        "     agreements.\n" +
        "\n" +
        "  e. Exceptions and Limitations means fair use, fair dealing, and/or\n" +
        "     any other exception or limitation to Copyright and Similar Rights\n" +
        "     that applies to Your use of the Licensed Material.\n" +
        "\n" +
        "  f. Licensed Material means the artistic or literary work, database,\n" +
        "     or other material to which the Licensor applied this Public\n" +
        "     License.\n" +
        "\n" +
        "  g. Licensed Rights means the rights granted to You subject to the\n" +
        "     terms and conditions of this Public License, which are limited to\n" +
        "     all Copyright and Similar Rights that apply to Your use of the\n" +
        "     Licensed Material and that the Licensor has authority to license.\n" +
        "\n" +
        "  h. Licensor means the individual(s) or entity(ies) granting rights\n" +
        "     under this Public License.\n" +
        "\n" +
        "  i. Share means to provide material to the public by any means or\n" +
        "     process that requires permission under the Licensed Rights, such\n" +
        "     as reproduction, public display, public performance, distribution,\n" +
        "     dissemination, communication, or importation, and to make material\n" +
        "     available to the public including in ways that members of the\n" +
        "     public may access the material from a place and at a time\n" +
        "     individually chosen by them.\n" +
        "\n" +
        "  j. Sui Generis Database Rights means rights other than copyright\n" +
        "     resulting from Directive 96/9/EC of the European Parliament and of\n" +
        "     the Council of 11 March 1996 on the legal protection of databases,\n" +
        "     as amended and/or succeeded, as well as other essentially\n" +
        "     equivalent rights anywhere in the world.\n" +
        "\n" +
        "  k. You means the individual or entity exercising the Licensed Rights\n" +
        "     under this Public License. Your has a corresponding meaning.\n" +
        "\n" +
        "\n" +
        "Section 2 -- Scope.\n" +
        "\n" +
        "  a. License grant.\n" +
        "\n" +
        "       1. Subject to the terms and conditions of this Public License,\n" +
        "          the Licensor hereby grants You a worldwide, royalty-free,\n" +
        "          non-sublicensable, non-exclusive, irrevocable license to\n" +
        "          exercise the Licensed Rights in the Licensed Material to:\n" +
        "\n" +
        "            a. reproduce and Share the Licensed Material, in whole or\n" +
        "               in part; and\n" +
        "\n" +
        "            b. produce, reproduce, and Share Adapted Material.\n" +
        "\n" +
        "       2. Exceptions and Limitations. For the avoidance of doubt, where\n" +
        "          Exceptions and Limitations apply to Your use, this Public\n" +
        "          License does not apply, and You do not need to comply with\n" +
        "          its terms and conditions.\n" +
        "\n" +
        "       3. Term. The term of this Public License is specified in Section\n" +
        "          6(a).\n" +
        "\n" +
        "       4. Media and formats; technical modifications allowed. The\n" +
        "          Licensor authorizes You to exercise the Licensed Rights in\n" +
        "          all media and formats whether now known or hereafter created,\n" +
        "          and to make technical modifications necessary to do so. The\n" +
        "          Licensor waives and/or agrees not to assert any right or\n" +
        "          authority to forbid You from making technical modifications\n" +
        "          necessary to exercise the Licensed Rights, including\n" +
        "          technical modifications necessary to circumvent Effective\n" +
        "          Technological Measures. For purposes of this Public License,\n" +
        "          simply making modifications authorized by this Section 2(a)\n" +
        "          (4) never produces Adapted Material.\n" +
        "\n" +
        "       5. Downstream recipients.\n" +
        "\n" +
        "            a. Offer from the Licensor -- Licensed Material. Every\n" +
        "               recipient of the Licensed Material automatically\n" +
        "               receives an offer from the Licensor to exercise the\n" +
        "               Licensed Rights under the terms and conditions of this\n" +
        "               Public License.\n" +
        "\n" +
        "            b. No downstream restrictions. You may not offer or impose\n" +
        "               any additional or different terms or conditions on, or\n" +
        "               apply any Effective Technological Measures to, the\n" +
        "               Licensed Material if doing so restricts exercise of the\n" +
        "               Licensed Rights by any recipient of the Licensed\n" +
        "               Material.\n" +
        "\n" +
        "       6. No endorsement. Nothing in this Public License constitutes or\n" +
        "          may be construed as permission to assert or imply that You\n" +
        "          are, or that Your use of the Licensed Material is, connected\n" +
        "          with, or sponsored, endorsed, or granted official status by,\n" +
        "          the Licensor or others designated to receive attribution as\n" +
        "          provided in Section 3(a)(1)(A)(i).\n" +
        "\n" +
        "  b. Other rights.\n" +
        "\n" +
        "       1. Moral rights, such as the right of integrity, are not\n" +
        "          licensed under this Public License, nor are publicity,\n" +
        "          privacy, and/or other similar personality rights; however, to\n" +
        "          the extent possible, the Licensor waives and/or agrees not to\n" +
        "          assert any such rights held by the Licensor to the limited\n" +
        "          extent necessary to allow You to exercise the Licensed\n" +
        "          Rights, but not otherwise.\n" +
        "\n" +
        "       2. Patent and trademark rights are not licensed under this\n" +
        "          Public License.\n" +
        "\n" +
        "       3. To the extent possible, the Licensor waives any right to\n" +
        "          collect royalties from You for the exercise of the Licensed\n" +
        "          Rights, whether directly or through a collecting society\n" +
        "          under any voluntary or waivable statutory or compulsory\n" +
        "          licensing scheme. In all other cases the Licensor expressly\n" +
        "          reserves any right to collect such royalties.\n" +
        "\n" +
        "\n" +
        "Section 3 -- License Conditions.\n" +
        "\n" +
        "Your exercise of the Licensed Rights is expressly made subject to the\n" +
        "following conditions.\n" +
        "\n" +
        "  a. Attribution.\n" +
        "\n" +
        "       1. If You Share the Licensed Material (including in modified\n" +
        "          form), You must:\n" +
        "\n" +
        "            a. retain the following if it is supplied by the Licensor\n" +
        "               with the Licensed Material:\n" +
        "\n" +
        "                 i. identification of the creator(s) of the Licensed\n" +
        "                    Material and any others designated to receive\n" +
        "                    attribution, in any reasonable manner requested by\n" +
        "                    the Licensor (including by pseudonym if\n" +
        "                    designated);\n" +
        "\n" +
        "                ii. a copyright notice;\n" +
        "\n" +
        "               iii. a notice that refers to this Public License;\n" +
        "\n" +
        "                iv. a notice that refers to the disclaimer of\n" +
        "                    warranties;\n" +
        "\n" +
        "                 v. a URI or hyperlink to the Licensed Material to the\n" +
        "                    extent reasonably practicable;\n" +
        "\n" +
        "            b. indicate if You modified the Licensed Material and\n" +
        "               retain an indication of any previous modifications; and\n" +
        "\n" +
        "            c. indicate the Licensed Material is licensed under this\n" +
        "               Public License, and include the text of, or the URI or\n" +
        "               hyperlink to, this Public License.\n" +
        "\n" +
        "       2. You may satisfy the conditions in Section 3(a)(1) in any\n" +
        "          reasonable manner based on the medium, means, and context in\n" +
        "          which You Share the Licensed Material. For example, it may be\n" +
        "          reasonable to satisfy the conditions by providing a URI or\n" +
        "          hyperlink to a resource that includes the required\n" +
        "          information.\n" +
        "\n" +
        "       3. If requested by the Licensor, You must remove any of the\n" +
        "          information required by Section 3(a)(1)(A) to the extent\n" +
        "          reasonably practicable.\n" +
        "\n" +
        "       4. If You Share Adapted Material You produce, the Adapter's\n" +
        "          License You apply must not prevent recipients of the Adapted\n" +
        "          Material from complying with this Public License.\n" +
        "\n" +
        "\n" +
        "Section 4 -- Sui Generis Database Rights.\n" +
        "\n" +
        "Where the Licensed Rights include Sui Generis Database Rights that\n" +
        "apply to Your use of the Licensed Material:\n" +
        "\n" +
        "  a. for the avoidance of doubt, Section 2(a)(1) grants You the right\n" +
        "     to extract, reuse, reproduce, and Share all or a substantial\n" +
        "     portion of the contents of the database;\n" +
        "\n" +
        "  b. if You include all or a substantial portion of the database\n" +
        "     contents in a database in which You have Sui Generis Database\n" +
        "     Rights, then the database in which You have Sui Generis Database\n" +
        "     Rights (but not its individual contents) is Adapted Material; and\n" +
        "\n" +
        "  c. You must comply with the conditions in Section 3(a) if You Share\n" +
        "     all or a substantial portion of the contents of the database.\n" +
        "\n" +
        "For the avoidance of doubt, this Section 4 supplements and does not\n" +
        "replace Your obligations under this Public License where the Licensed\n" +
        "Rights include other Copyright and Similar Rights.\n" +
        "\n" +
        "\n" +
        "Section 5 -- Disclaimer of Warranties and Limitation of Liability.\n" +
        "\n" +
        "  a. UNLESS OTHERWISE SEPARATELY UNDERTAKEN BY THE LICENSOR, TO THE\n" +
        "     EXTENT POSSIBLE, THE LICENSOR OFFERS THE LICENSED MATERIAL AS-IS\n" +
        "     AND AS-AVAILABLE, AND MAKES NO REPRESENTATIONS OR WARRANTIES OF\n" +
        "     ANY KIND CONCERNING THE LICENSED MATERIAL, WHETHER EXPRESS,\n" +
        "     IMPLIED, STATUTORY, OR OTHER. THIS INCLUDES, WITHOUT LIMITATION,\n" +
        "     WARRANTIES OF TITLE, MERCHANTABILITY, FITNESS FOR A PARTICULAR\n" +
        "     PURPOSE, NON-INFRINGEMENT, ABSENCE OF LATENT OR OTHER DEFECTS,\n" +
        "     ACCURACY, OR THE PRESENCE OR ABSENCE OF ERRORS, WHETHER OR NOT\n" +
        "     KNOWN OR DISCOVERABLE. WHERE DISCLAIMERS OF WARRANTIES ARE NOT\n" +
        "     ALLOWED IN FULL OR IN PART, THIS DISCLAIMER MAY NOT APPLY TO YOU.\n" +
        "\n" +
        "  b. TO THE EXTENT POSSIBLE, IN NO EVENT WILL THE LICENSOR BE LIABLE\n" +
        "     TO YOU ON ANY LEGAL THEORY (INCLUDING, WITHOUT LIMITATION,\n" +
        "     NEGLIGENCE) OR OTHERWISE FOR ANY DIRECT, SPECIAL, INDIRECT,\n" +
        "     INCIDENTAL, CONSEQUENTIAL, PUNITIVE, EXEMPLARY, OR OTHER LOSSES,\n" +
        "     COSTS, EXPENSES, OR DAMAGES ARISING OUT OF THIS PUBLIC LICENSE OR\n" +
        "     USE OF THE LICENSED MATERIAL, EVEN IF THE LICENSOR HAS BEEN\n" +
        "     ADVISED OF THE POSSIBILITY OF SUCH LOSSES, COSTS, EXPENSES, OR\n" +
        "     DAMAGES. WHERE A LIMITATION OF LIABILITY IS NOT ALLOWED IN FULL OR\n" +
        "     IN PART, THIS LIMITATION MAY NOT APPLY TO YOU.\n" +
        "\n" +
        "  c. The disclaimer of warranties and limitation of liability provided\n" +
        "     above shall be interpreted in a manner that, to the extent\n" +
        "     possible, most closely approximates an absolute disclaimer and\n" +
        "     waiver of all liability.\n" +
        "\n" +
        "\n" +
        "Section 6 -- Term and Termination.\n" +
        "\n" +
        "  a. This Public License applies for the term of the Copyright and\n" +
        "     Similar Rights licensed here. However, if You fail to comply with\n" +
        "     this Public License, then Your rights under this Public License\n" +
        "     terminate automatically.\n" +
        "\n" +
        "  b. Where Your right to use the Licensed Material has terminated under\n" +
        "     Section 6(a), it reinstates:\n" +
        "\n" +
        "       1. automatically as of the date the violation is cured, provided\n" +
        "          it is cured within 30 days of Your discovery of the\n" +
        "          violation; or\n" +
        "\n" +
        "       2. upon express reinstatement by the Licensor.\n" +
        "\n" +
        "     For the avoidance of doubt, this Section 6(b) does not affect any\n" +
        "     right the Licensor may have to seek remedies for Your violations\n" +
        "     of this Public License.\n" +
        "\n" +
        "  c. For the avoidance of doubt, the Licensor may also offer the\n" +
        "     Licensed Material under separate terms or conditions or stop\n" +
        "     distributing the Licensed Material at any time; however, doing so\n" +
        "     will not terminate this Public License.\n" +
        "\n" +
        "  d. Sections 1, 5, 6, 7, and 8 survive termination of this Public\n" +
        "     License.\n" +
        "\n" +
        "\n" +
        "Section 7 -- Other Terms and Conditions.\n" +
        "\n" +
        "  a. The Licensor shall not be bound by any additional or different\n" +
        "     terms or conditions communicated by You unless expressly agreed.\n" +
        "\n" +
        "  b. Any arrangements, understandings, or agreements regarding the\n" +
        "     Licensed Material not stated herein are separate from and\n" +
        "     independent of the terms and conditions of this Public License.\n" +
        "\n" +
        "\n" +
        "Section 8 -- Interpretation.\n" +
        "\n" +
        "  a. For the avoidance of doubt, this Public License does not, and\n" +
        "     shall not be interpreted to, reduce, limit, restrict, or impose\n" +
        "     conditions on any use of the Licensed Material that could lawfully\n" +
        "     be made without permission under this Public License.\n" +
        "\n" +
        "  b. To the extent possible, if any provision of this Public License is\n" +
        "     deemed unenforceable, it shall be automatically reformed to the\n" +
        "     minimum extent necessary to make it enforceable. If the provision\n" +
        "     cannot be reformed, it shall be severed from this Public License\n" +
        "     without affecting the enforceability of the remaining terms and\n" +
        "     conditions.\n" +
        "\n" +
        "  c. No term or condition of this Public License will be waived and no\n" +
        "     failure to comply consented to unless expressly agreed to by the\n" +
        "     Licensor.\n" +
        "\n" +
        "  d. Nothing in this Public License constitutes or may be interpreted\n" +
        "     as a limitation upon, or waiver of, any privileges and immunities\n" +
        "     that apply to the Licensor or You, including from the legal\n" +
        "     processes of any jurisdiction or authority.\n" +
        "\n" +
        "\n" +
        "=======================================================================\n" +
        "\n" +
        "Creative Commons is not a party to its public licenses.\n" +
        "Notwithstanding, Creative Commons may elect to apply one of its public\n" +
        "licenses to material it publishes and in those instances will be\n" +
        "considered the \"Licensor.\" Except for the limited purpose of indicating\n" +
        "that material is shared under a Creative Commons public license or as\n" +
        "otherwise permitted by the Creative Commons policies published at\n" +
        "creativecommons.org/policies, Creative Commons does not authorize the\n" +
        "use of the trademark \"Creative Commons\" or any other trademark or logo\n" +
        "of Creative Commons without its prior written consent including,\n" +
        "without limitation, in connection with any unauthorized modifications\n" +
        "to any of its public licenses or any other arrangements,\n" +
        "understandings, or agreements concerning use of licensed material. For\n" +
        "the avoidance of doubt, this paragraph does not form part of the public\n" +
        "licenses.\n" +
        "\n" +
        "Creative Commons may be contacted at creativecommons.org.\n");
  }
}
